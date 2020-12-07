package com.example.feelings_diary

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import java.util.*
import kotlin.collections.ArrayList

class TCICommentActivity : AppCompatActivity() {
    private var backButton: Button? = null
    private var uid: String? = null
    private var uemail:String? = null
    private var puid: String? = null
    private var puemail: String? = null
    private var titleView: TextView? = null

    private var databaseDiary: DatabaseReference? = null
    private lateinit var diaryEntries: MutableList<DiaryEntry>
    private var checkListView: ListView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.checkin_listview_therapist)
        uid = intent.getStringExtra(USER_ID)
        uemail = intent.getStringExtra(USER_EMAIL)
        puid = intent.getStringExtra(TherapistHomeActivity.CURR_PATIENT_ID)
        puemail = intent.getStringExtra(TherapistHomeActivity.CURR_PATIENT_EMAIL)

        checkListView = findViewById(R.id.tlistViewCheckIns)

        titleView = findViewById(R.id.tscreen_title)

        backButton = findViewById(R.id.tback_button)
        databaseDiary = FirebaseDatabase.getInstance().getReference("diary")
        diaryEntries = ArrayList()
        titleView!!.text = "Patient's Check-Ins"


        backButton!!.setOnClickListener{
            startActivity(
                Intent(this@TCICommentActivity, TherapistHomeActivity::class.java).putExtra(
                    CheckInActivity.USER_ID, uid).putExtra(
                    CheckInActivity.USER_EMAIL, uemail)
            )
        }
    }

    override fun onStart(){
        super.onStart()

        databaseDiary!!.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                diaryEntries!!.clear()
                Log.i(TAG, puid)
                var entry: DiaryEntry? = null
                for(data in snapshot.child(puid!!).children){
                    try{
                        entry = data.getValue(DiaryEntry::class.java)
                    }catch(e:Exception){
                        Log.e(TAG,e.toString())
                    }finally{
                        diaryEntries!!.add(entry!!)
                    }
                }

                var sortedEntries = diaryEntries!!.sortedByDescending { it.long_date }
                diaryEntries = sortedEntries.toMutableList()
                val checkAdapter = CheckInAdapter(this@TCICommentActivity, diaryEntries, puid)
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