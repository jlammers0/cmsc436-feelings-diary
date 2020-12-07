package com.example.feelings_diary

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import java.text.DateFormat
import java.util.*
import kotlin.collections.ArrayList

class CheckInListActivity : AppCompatActivity(){
    private var backButton: Button? = null
    private var uid: String? = null
    private var uemail:String? = null
    private var sDateString:String? = null
    private var selectedDate: Calendar? = null
    private var databaseDiary: DatabaseReference? = null
    private lateinit var diaryEntries: MutableList<DiaryEntry>
    private var checkListView: ListView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.checkin_listview_patient)
        uid = intent.getStringExtra(USER_ID)
        uemail = intent.getStringExtra(USER_EMAIL)
        sDateString = intent.getStringExtra(DATE_SELECTED)
        checkListView = findViewById(R.id.listViewCheckIns)
        selectedDate = Calendar.getInstance()
        selectedDate!!.timeInMillis = sDateString!!.toLong()

        backButton = findViewById(R.id.back_button)

        databaseDiary = FirebaseDatabase.getInstance().getReference("diary")
        diaryEntries = ArrayList()

        backButton!!.setOnClickListener{
            startActivity(
                Intent(this@CheckInListActivity, PatientHomeActivity::class.java).putExtra(
                    CheckInActivity.USER_ID, uid).putExtra(
                    CheckInActivity.USER_EMAIL, uemail)
            )
        }
    }

    override fun onStart(){
        super.onStart()

        databaseDiary!!.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                diaryEntries!!.clear()

                var entry: DiaryEntry? = null
                for(data in snapshot.child(uid!!).children){
                    try{
                        entry = data.getValue(DiaryEntry::class.java)
                    }catch(e:Exception){
                        Log.e(TAG,e.toString())
                    }finally{
                        diaryEntries!!.add(entry!!)
                    }
                }

                var daysEntries = ArrayList<DiaryEntry>()

                for(ent in diaryEntries){

                    var curCalDate = Calendar.getInstance()
                    curCalDate.timeInMillis = ent.long_date
                    if(selectedDate!!.get(Calendar.DAY_OF_YEAR) == curCalDate.get(Calendar.DAY_OF_YEAR) &&
                            selectedDate!!.get(Calendar.YEAR) == curCalDate.get(Calendar.YEAR)){
                        daysEntries.add(ent)
                    }
                }
                val checkAdapter = CheckInAdapter(this@CheckInListActivity, daysEntries)
                checkListView!!.adapter = checkAdapter
            }

            override fun onCancelled(error: DatabaseError){
                Log.i(TAG, "loading user's checkin activities was cancelled")
            }
        })


    }

    companion object{
        const val TAG = "feelings-diary-log"
        const val USER_ID = "com.example.tesla.myhomelibrary.userid"
        const val USER_EMAIL = "com.example.tesla.myhomelibrary.useremail"
        const val DATE_SELECTED = "date selected"
    }
}