package com.example.feelings_diary

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import java.util.*

class MailMessageActivity: AppCompatActivity() {

    private var mailDateView: TextView? = null
    private var mailFromView: TextView? = null
    private var mailTypeView: TextView? = null
    private var mailSubjectView: TextView? = null
    private var mailBodyView: TextView? = null
    private var replyButton: Button? = null
    private var deleteButton: Button? = null
    private var databaseInbox: DatabaseReference? = null
    private var uid: String? = null
    private var uemail:String? = null
    private var linearLayout: LinearLayout? = null
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mail_message_view)

        linearLayout = findViewById(R.id.mailMessageView)

        mailDateView = findViewById(R.id.mailDate)
        mailFromView = findViewById(R.id.mailFrom)
        mailTypeView = findViewById(R.id.mailType)
        mailSubjectView = findViewById(R.id.mailSubject)
        mailBodyView = findViewById(R.id.mailBody)
        replyButton = findViewById(R.id.replyButton)
        deleteButton = findViewById(R.id.deleteButton)
        databaseInbox = FirebaseDatabase.getInstance().getReference("inbox")

        uid = intent.getStringExtra(USER_ID)
        uemail = intent.getStringExtra(USER_EMAIL)



        mailDateView!!.text = intent.getStringExtra("date")
        mailFromView!!.text = intent.getStringExtra("from")
        mailTypeView!!.text = intent.getStringExtra("type")
        mailSubjectView!!.text = intent.getStringExtra("subject")
        mailBodyView!!.text = intent.getStringExtra("body")

        deleteButton!!.setOnClickListener{
            val date = intent.getStringExtra("date")
           FirebaseDatabase.getInstance().reference.child("inbox").child(uid!!).child(date!!).removeValue()
            startActivity(Intent(this@MailMessageActivity,MailInboxActivity::class.java).putExtra(
                USER_ID,uid))
        }

        replyButton!!.setOnClickListener{

            if (intent.getStringExtra("from").equals("Feelings Diary Team",true)){
                Toast.makeText(applicationContext,"This message is from an automated user which cannot receive replies.",Toast.LENGTH_LONG).show()
                Log.i(TAG,"User tried to reply to Feelings Diary Team")
            }else{
                val messageIntent = Intent(this@MailMessageActivity,MailReplyActivity::class.java)
                messageIntent.putExtra(USER_ID,uid)
                messageIntent.putExtra(USER_EMAIL,uemail)
                messageIntent.putExtra("date",intent.getStringExtra("date"))
                messageIntent.putExtra("from",intent.getStringExtra("from"))
                messageIntent.putExtra("type",intent.getStringExtra("type"))
                messageIntent.putExtra("subject",intent.getStringExtra("subject"))
                messageIntent.putExtra("body",intent.getStringExtra("body"))
                startActivity(messageIntent)
            }



        }

        if(intent.getStringExtra("type").equals("MEETINGREQUEST",true)){
            Log.i(TAG,"Creating buttons")
            val layout = findViewById(R.id.mailMessageView) as LinearLayout
            val meetingDateView = TextView(this)
            meetingDateView.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT)

            val mMeetingDate = intent.getStringExtra("meeting")
            val newmMeetingDate = "Meeting Request Date: ${mMeetingDate}"
            meetingDateView.text = newmMeetingDate
            layout.addView(meetingDateView)
            val scheduleMeetingButton = Button(this)
            scheduleMeetingButton.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT)
            scheduleMeetingButton.text = "Add meeting to calendar"
            scheduleMeetingButton.setOnClickListener{
                // TODO: Add to google calendar https://itnext.io/android-calendar-intent-8536232ecb38
            }
            layout.addView(scheduleMeetingButton)
        }

    }

    companion object{
        const val USER_ID = "com.example.tesla.myhomelibrary.userid"
        const val TAG = "feelings-diary-log"
        const val USER_EMAIL = "com.example.tesla.myhomelibrary.useremail"
    }
}