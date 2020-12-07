package com.example.feelings_diary

import android.content.ContentUris
import android.content.Intent
import android.os.Bundle
import android.provider.CalendarContract
import android.util.Log
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import java.text.SimpleDateFormat
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
                val fromUid = intent.getStringExtra("from")
                val dateString = intent.getStringExtra("meeting")
                val dateFormatted = SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyy").parse(dateString!!)
                Log.i(TAG,dateFormatted.toString())

                var calendar = Calendar.getInstance()
                calendar.clear()
                calendar.set(dateFormatted.year+1900,dateFormatted.month,dateFormatted.date,dateFormatted.hours,dateFormatted.minutes)

                Log.i(TAG,calendar.toString())


                val insertCalendarIntent = Intent(Intent.ACTION_INSERT).setData(CalendarContract.Events.CONTENT_URI)
                    .putExtra(CalendarContract.Events.TITLE, "Therapy Meeting with $fromUid")
                    .putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, false)
                    .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,calendar.timeInMillis) // Only date part is considered when ALL_DAY is true; Same as DTSTART
                    .putExtra(CalendarContract.Events.DESCRIPTION, intent.getStringExtra("body")) // Description
                    .putExtra(Intent.EXTRA_EMAIL, fromUid)

                startActivity(insertCalendarIntent)
                val calendarEventTime = System.currentTimeMillis()

                val builder = CalendarContract.CONTENT_URI.buildUpon().appendPath("time")
                ContentUris.appendId(builder,calendarEventTime)
                val uri = builder.build()
                val intent = Intent(Intent.ACTION_VIEW).setData(uri)
                startActivity(intent)
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