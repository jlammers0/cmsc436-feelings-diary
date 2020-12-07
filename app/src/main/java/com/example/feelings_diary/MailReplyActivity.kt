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
class MailReplyActivity: AppCompatActivity(), DatePickerDialog.OnDateSetListener,
TimePickerDialog.OnTimeSetListener {

    private var rspMailDateView: TextView? = null
    private var rspMailToView:TextView? = null
    private var rspMailTypeView: Spinner? = null
    private var rspMailSubjectView: EditText? = null
    private var rspMailBodyView: EditText? = null
    private var sendButton: Button? = null
    private var cancelButton: Button? = null
    private lateinit var userList: MutableList<User>

    private var mailDateView: TextView? = null
    private var mailFromView: TextView? = null
    private var mailTypeView: TextView? = null
    private var mailSubjectView: TextView? = null
    private var mailBodyView: TextView? = null
    private var meetDate: String? = null
    private var myDay: Int = 0
    private var myMonth: Int = 0
    private var myYear: Int = 0
    private var myHour: Int = 0
    private var myMinute: Int = 0


    private var uid:String? = null
    private var uemail:String? = null

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mail_reply)

        rspMailDateView = findViewById(R.id.rspMailDate)
        rspMailToView = findViewById(R.id.rspMailTo)
        rspMailTypeView = findViewById(R.id.rspMailType)
        rspMailSubjectView = findViewById(R.id.rspMailSubject)
        rspMailBodyView = findViewById(R.id.rspMailBody)
        sendButton = findViewById(R.id.sendButton)
        cancelButton = findViewById(R.id.cancelButton)
        userList = ArrayList()

        mailDateView = findViewById(R.id.mailDate)
        mailFromView = findViewById(R.id.mailFrom)
        mailTypeView = findViewById(R.id.mailType)
        mailSubjectView = findViewById(R.id.mailSubject)
        mailBodyView = findViewById(R.id.mailBody)
        meetDate = ""


        val mDate = intent.getStringExtra("date")
        val displayDate = "Date: $mDate"
        val mFrom = intent.getStringExtra("from")
        val displayFrom = "From: $mFrom"
        val mType = intent.getStringExtra("type")
        var displayType = "Type: $mType"
        val mSubject = intent.getStringExtra("subject")
        val displaySubject = "Subject: $mSubject"
        val mBody = intent.getStringExtra("body")
        val displayBody = "Body: $mBody"
        mailDateView!!.text = displayDate
        mailFromView!!.text = displayFrom
        mailTypeView!!.text = displayType
        mailSubjectView!!.text = displaySubject
        mailBodyView!!.text = displayBody

        uid = intent.getStringExtra(USER_ID)
        uemail = intent.getStringExtra(USER_EMAIL)

        rspMailToView!!.text = intent.getStringExtra("from")
        rspMailDateView!!.text = Date(System.currentTimeMillis()).toString()

        val oldSubject = intent.getStringExtra("subject")
        val newSubject = "RE: $oldSubject"
        rspMailSubjectView!!.setText(newSubject)

        rspMailTypeView!!.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
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

        sendButton!!.setOnClickListener{
            var type = MessageType.MESSAGE
            if(rspMailTypeView!!.selectedItem.toString().replace("\\s".toRegex(), "").equals(MessageType.MESSAGE.toString(),true)){
                type = MessageType.MESSAGE
            }else if (rspMailTypeView!!.selectedItem.toString().replace("\\s".toRegex(), "").equals(MessageType.MEETINGREQUEST.toString(),true)){
                type = MessageType.MEETINGREQUEST
            }else{
                Log.i(NewMessageActivity.TAG,"Spinner selected message type didn't match either type")
            }

            var toUser: User?= null
            for (x in userList){
                if (x.email == rspMailToView!!.text.toString()){
                    toUser = x
                }
            }
            Log.i(TAG,rspMailDateView!!.text.toString() )
            Log.i(TAG,uemail!!)
            Log.i(TAG,rspMailToView!!.text.toString())
            Log.i(TAG,type.toString())
            Log.i(TAG,rspMailSubjectView!!.text.toString())
            Log.i(TAG,rspMailBodyView!!.text.toString())


            val message = Message(rspMailDateView!!.text.toString(),uemail!!,rspMailToView!!.text.toString(),type,rspMailSubjectView!!.text.toString(),rspMailBodyView!!.text.toString(),meetDate!!)
            FirebaseDatabase.getInstance().reference.child("inbox").child(toUser!!.uid).child(rspMailDateView!!.text.toString()).setValue(message)
            Toast.makeText(applicationContext,"Message has been sent", Toast.LENGTH_SHORT).show()
            startActivity(
                Intent(this@MailReplyActivity,MailInboxActivity::class.java).putExtra(
                    NewMessageActivity.USER_ID,uid).putExtra(NewMessageActivity.USER_EMAIL,uemail))
        }
        cancelButton!!.setOnClickListener{
            Toast.makeText(applicationContext,"Message was not sent", Toast.LENGTH_SHORT).show()
            startActivity(
                Intent(this@MailReplyActivity,MailInboxActivity::class.java).putExtra(
                    NewMessageActivity.USER_ID,uid).putExtra(NewMessageActivity.USER_EMAIL,uemail))

        }



    }
    private fun showDatePickerDialog(){


        val calendar: Calendar = Calendar.getInstance()
        val datePicker = DatePickerDialog(this@MailReplyActivity,this@MailReplyActivity,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH))
        datePicker.show()
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        myDay = dayOfMonth
        myYear = year
        myMonth = month
        val calendar: Calendar = Calendar.getInstance()

        Log.i(TAG,"MY year = ${myYear}")

        val timePickerDialog = TimePickerDialog(this@MailReplyActivity, this@MailReplyActivity, calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE),
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
                        Log.e(NewMessageActivity.TAG,e.toString())
                    }finally{
                        userList.add(user!!)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    companion object{
        const val USER_ID = "com.example.tesla.myhomelibrary.userid"
        const val USER_EMAIL = "com.example.tesla.myhomelibrary.useremail"
        const val TAG = "feelings-diary-log"
    }
}