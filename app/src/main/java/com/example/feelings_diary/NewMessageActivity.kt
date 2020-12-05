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

class NewMessageActivity:AppCompatActivity() {

    private var dateView: TextView? = null
    private var toView: EditText? = null
    private var typeView: Spinner?=null
    private var subjectView: EditText? = null
    private var bodyView: EditText? = null
    private var sendButton: Button?=null
    private var cancelButton: Button? =null
    private var uid:String?=null
    private var uemail:String?=null


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

       dateView!!.text = Date(System.currentTimeMillis()).toString()

        sendButton!!.setOnClickListener{
            var type = MessageType.MESSAGE
           if(typeView!!.selectedItem.toString().equals(MessageType.MESSAGE.toString(),true)){
               type = MessageType.MESSAGE
           }else if (typeView!!.selectedItem.toString().equals(MessageType.MEETINGREQUEST.toString(),true)){
               type = MessageType.MEETINGREQUEST
           }else{
               Log.i(TAG,"Spinner selected message type didn't match either type")
           }

            val toUser = getUserFromEmail(toView!!.text.toString())



            val message = Message(dateView!!.text.toString(),uemail!!,toView!!.text.toString(),type,subjectView!!.text.toString(),bodyView!!.text.toString())
            FirebaseDatabase.getInstance().reference.child("inbox").child(toUser!!.uid).child(dateView!!.text.toString()).setValue(message)
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
                Log.i(MailReplyActivity.TAG,"fetching users failed")
            }

        })

        return user
    }
    companion object{
        const val TAG = "feelings-diary-log"
        const val USER_ID = "com.example.tesla.myhomelibrary.userid"
        const val USER_EMAIL = "com.example.tesla.myhomelibrary.useremail"
    }
}