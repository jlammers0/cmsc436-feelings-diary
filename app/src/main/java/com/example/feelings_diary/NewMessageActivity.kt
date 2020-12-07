package com.example.feelings_diary

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*

//DatePicker and time picker code taken from
//https://www.tutorialspoint.com/how-to-use-date-time-picker-dialog-in-kotlin-android

class NewMessageActivity:AppCompatActivity(),DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener {

    private var dateView: TextView? = null
    private var toView: EditText? = null
    private var typeView: Spinner?=null
    private var subjectView: EditText? = null
    private var bodyView: EditText? = null
    private var sendButton: Button?=null
    private var cancelButton: Button? =null
    private var uid:String?=null
    private var uemail:String?=null
    private lateinit var userList:MutableList<User>
    private var meetDate: String? = null
    private var myDay: Int = 0
    private var myMonth: Int = 0
    private var myYear: Int = 0
    private var myHour: Int = 0
    private var myMinute: Int = 0


    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.compose_new_message)

        dateView = findViewById(R.id.newMailDate)
        toView = findViewById(R.id.newMailTo)
        typeView = findViewById(R.id.newMailType)
        subjectView = findViewById(R.id.newMailSubject)
        bodyView = findViewById(R.id.newMailBody)
        sendButton = findViewById(R.id.sendButton)
        cancelButton = findViewById(R.id.cancelButton)
        uid = intent.getStringExtra(USER_ID)
        uemail=intent.getStringExtra(USER_EMAIL)
        userList = ArrayList()
        meetDate = ""

        if (intent.getStringExtra("TO")!= null){
            toView!!.setText(intent.getStringExtra("TO"))
        }
        if (intent.getStringExtra("SUBJECT")!=null){
            subjectView!!.setText(intent.getStringExtra("SUBJECT"))
            typeView!!.setSelection(1)



        }

        typeView!!.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (p2==1){
                    showDatePickerDialog()
                }
                if (p2==0){
                    meetDate = ""
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }



       dateView!!.text = Date(System.currentTimeMillis()).toString()

        sendButton!!.setOnClickListener{
            var type = MessageType.MESSAGE

           if(typeView!!.selectedItem.toString().replace("\\s".toRegex(), "").equals(MessageType.MESSAGE.toString(),true)){
               type = MessageType.MESSAGE
           }else if (typeView!!.selectedItem.toString().replace("\\s".toRegex(), "").equals(MessageType.MEETINGREQUEST.toString(),true)){
               type = MessageType.MEETINGREQUEST
           }else{
               Log.i(TAG,"Spinner selected message type didn't match either type")
           }

            var toUser: User?= null
            for (x in userList){
                if (x.email == toView!!.text.toString()){
                    toUser = x
                }
            }


            Log.i(TAG,toView!!.text.toString())
            Log.i(TAG,toUser!!.email)



            val message = Message(dateView!!.text.toString(),uemail!!,toView!!.text.toString(),type,subjectView!!.text.toString(),bodyView!!.text.toString(),meetDate!!)
            FirebaseDatabase.getInstance().reference.child("inbox").child(toUser.uid).child(dateView!!.text.toString()).setValue(message)
            Toast.makeText(applicationContext,"Message has been sent", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this@NewMessageActivity,MailInboxActivity::class.java).putExtra(
                USER_ID,uid).putExtra(USER_EMAIL,uemail))
        }
        cancelButton!!.setOnClickListener{
            Toast.makeText(applicationContext,"Message was not sent",Toast.LENGTH_SHORT).show()
            startActivity(Intent(this@NewMessageActivity,MailInboxActivity::class.java).putExtra(
                USER_ID,uid).putExtra(USER_EMAIL,uemail))

        }


    }

    private fun showDatePickerDialog(){


        val calendar: Calendar = Calendar.getInstance()
        val datePicker = DatePickerDialog(this@NewMessageActivity,this@NewMessageActivity,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH))
        datePicker.show()
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        myDay = dayOfMonth
        myYear = year
        myMonth = month
        val calendar: Calendar = Calendar.getInstance()
        val timePickerDialog = TimePickerDialog(this@NewMessageActivity, this@NewMessageActivity, calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE),
            false)
        timePickerDialog.show()
    }
    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        myHour = hourOfDay
        myMinute = minute
        meetDate = Date(myYear-1900,myMonth,myDay,myHour,myMinute).toString()
    }

    override fun onStart() {
        super.onStart()
        FirebaseDatabase.getInstance().getReference("users").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                var user: User? = null
                for(data in snapshot.children){
                    try {
                        user = data.getValue(User::class.java)
                    }catch(e:Exception){
                        Log.e(TAG,e.toString())
                    }finally{
                        userList.add(user!!)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.i(TAG,"canceled firebase operation")
            }
        })
    }


    companion object{
        const val TAG = "feelings-diary-log"
        const val USER_ID = "com.example.tesla.myhomelibrary.userid"
        const val USER_EMAIL = "com.example.tesla.myhomelibrary.useremail"
    }
}