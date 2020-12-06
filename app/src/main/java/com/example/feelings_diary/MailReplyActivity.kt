package com.example.feelings_diary

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*

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

    private var uid:String? = null
    private var uemail:String? = null

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

        uid = intent.getStringExtra(USER_ID)
        uemail = intent.getStringExtra(USER_EMAIL)

        rspMailToView!!.text = intent.getStringExtra("from")
        rspMailDateView!!.text = Date(System.currentTimeMillis()).toString()
        rspMailSubjectView!!.setText("RE:" + mailSubjectView!!.text.toString())

        sendButton!!.setOnClickListener{
            var type = MessageType.MESSAGE
            if(rspMailTypeView!!.selectedItem.toString().equals(MessageType.MESSAGE.toString(),true)){
                type = MessageType.MESSAGE
            }else if (rspMailTypeView!!.selectedItem.toString().equals(MessageType.MEETINGREQUEST.toString(),true)){
                type = MessageType.MEETINGREQUEST
            }else{
                Log.i(NewMessageActivity.TAG,"Spinner selected message type didn't match either type")
            }

            val toUser = getUserFromEmail(rspMailToView!!.text.toString())



            val message = Message(rspMailDateView!!.text.toString(),uemail!!,rspMailToView!!.text.toString(),type,rspMailSubjectView!!.text.toString(),rspMailBodyView!!.text.toString())
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

    private fun getUserFromEmail(email:String): User? {
        var user: User? = null

        FirebaseDatabase.getInstance().getReference("users").addListenerForSingleValueEvent(object:
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                for (data in snapshot.children){
                    val temp = data.getValue(User::class.java)
                    if (user!!.email == email){
                        user=temp
                        break
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.i(TAG,"fetching users failed")
            }

        })

        return user
    }

    companion object{
        const val USER_ID = "com.example.tesla.myhomelibrary.userid"
        const val USER_EMAIL = "com.example.tesla.myhomelibrary.useremail"
        const val TAG = "feelings-diary-log"
    }
}