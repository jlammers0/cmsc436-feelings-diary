package com.example.feelings_diary

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*

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

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mail_view)

        mailDateView = findViewById(R.id.mailDate)
        mailFromView = findViewById(R.id.mailFrom)
        mailTypeView = findViewById(R.id.mailType)
        mailSubjectView = findViewById(R.id.mailSubject)
        mailBodyView = findViewById(R.id.mailBody)
        replyButton = findViewById(R.id.replyButton)
        deleteButton = findViewById(R.id.deleteButton)
        databaseInbox = FirebaseDatabase.getInstance().getReference("inbox")

        uid = intent.getStringExtra(USER_ID)



        mailDateView!!.text = intent.getStringExtra("date")
        mailFromView!!.text = intent.getStringExtra("from")
        mailTypeView!!.text = intent.getStringExtra("type")
        mailSubjectView!!.text = intent.getStringExtra("subject")
        mailBodyView!!.text = intent.getStringExtra("body")

        deleteButton!!.setOnClickListener{
           FirebaseDatabase.getInstance().reference.child("inbox").child(uid!!).child(intent.getStringExtra("date")).removeValue()
            startActivity(Intent(this@MailMessageActivity,MailInboxActivity::class.java))
        }

        replyButton!!.setOnClickListener{

            if (intent.getStringExtra("from").equals("Feelings Diary Team",true)){
                Toast.makeText(applicationContext,"This message is from an automated user which cannot receive replies.",Toast.LENGTH_LONG).show()

            }else{
                val messageIntent = Intent(this@MailMessageActivity,MailReplyActivity::class.java)
                messageIntent.putExtra(MailInboxActivity.USER_ID,uid)
                messageIntent.putExtra("date",intent.getStringExtra("date"))
                messageIntent.putExtra("from",intent.getStringExtra("from"))
                messageIntent.putExtra("type",intent.getStringExtra("type"))
                messageIntent.putExtra("subject",intent.getStringExtra("subject"))
                messageIntent.putExtra("body",intent.getStringExtra("body"))
            }

            

        }

    }

    companion object{
        const val USER_ID = "com.example.tesla.myhomelibrary.userid"
        const val TAG = "feelings-diary-log"
    }
}