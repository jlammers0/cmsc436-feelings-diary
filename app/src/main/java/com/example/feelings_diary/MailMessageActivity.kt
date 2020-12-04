package com.example.feelings_diary

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MailMessageActivity: AppCompatActivity() {

    private var mailDateView: TextView? = null
    private var mailFromView: TextView? = null
    private var mailTypeView: TextView? = null
    private var mailSubjectView: TextView? = null
    private var mailBodyView: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mail_view)

        mailDateView = findViewById(R.id.mailDate)
        mailFromView = findViewById(R.id.mailFrom)
        mailTypeView = findViewById(R.id.mailType)
        mailSubjectView = findViewById(R.id.mailSubject)
        mailBodyView = findViewById(R.id.mailBody)

        mailDateView!!.text = intent.getStringExtra("date")
        mailFromView!!.text = intent.getStringExtra("from")
        mailTypeView!!.text = intent.getStringExtra("type")
        mailSubjectView!!.text = intent.getStringExtra("subject")
        mailBodyView!!.text = intent.getStringExtra("body")

    }

    companion object{
        const val USER_ID = "com.example.tesla.myhomelibrary.userid"
        const val TAG = "feelings-diary-log"
    }
}