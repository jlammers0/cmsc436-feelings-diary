package com.example.feelings_diary

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MailReplyActivity: AppCompatActivity() {

    private var rspMailDateView: TextView? = null
    private var rspMailToView:TextView? = null
    private var rspMailTypeView: Spinner? = null
    private var rspMailSubjectView: EditText? = null
    private var rspMailBodyView: EditText? = null
    private var sendButton: Button? = null
    private var cancelButton: Button? = null

    private var mailDateView: TextView? = null
    private var mailFromView: TextView? = null
    private var mailTypeView: TextView? = null
    private var mailSubjectView: TextView? = null
    private var mailBodyView: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)

        rspMailDateView = findViewById(R.id.rspMailDate)
        rspMailToView = findViewById(R.id.rspMailTo)
        rspMailTypeView = findViewById(R.id.rspMailType)
        rspMailSubjectView = findViewById(R.id.rspMailSubject)
        rspMailBodyView = findViewById(R.id.rspMailBody)
        sendButton = findViewById(R.id.sendButton)
        cancelButton = findViewById(R.id.cancelButton)

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