package com.example.feelings_diary

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*




class MailInboxActivity : AppCompatActivity(){

    private var autoCompleteView: AutoCompleteTextView? = null
    private var autoCompleteCriteriaSpinner: Spinner? = null
    private var searchButton: Button? = null
    private var composeButton:Button? = null
    private var mailListView: ListView? = null
    private var messageList: MutableList<Message>? = null
    private var databaseInbox: DatabaseReference? = null
    private var uid: String? = null
    private var uemail:String?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.inbox_view)



        autoCompleteView = findViewById(R.id.autoCompleteView)
        autoCompleteCriteriaSpinner = findViewById(R.id.autoCompleteCriteria)
        searchButton = findViewById(R.id.searchButton)
        mailListView = findViewById(R.id.listViewMail)
        messageList = ArrayList()
        databaseInbox = FirebaseDatabase.getInstance().getReference("inbox")
        uid = intent.getStringExtra(USER_ID)
        uemail = intent.getStringExtra(USER_EMAIL)
        composeButton = findViewById(R.id.composeButton)

        //TODO: autoCompleteView needs an adapter to work. use autocompleteview and spinner to query mail

        searchButton!!.setOnClickListener {
            //TODO: needs autocompleteview and spinner. will launch a query and regenerate the listview
        }



        mailListView!!.onItemClickListener = AdapterView.OnItemClickListener{ _, _, i, _ ->
            val message = messageList!![i]

            val messageIntent = Intent(this@MailInboxActivity,MailMessageActivity::class.java)
            messageIntent.putExtra(USER_ID,uid)
            messageIntent.putExtra(USER_EMAIL,uemail)
            messageIntent.putExtra("date",message.date)
            messageIntent.putExtra("from",message.from)
            messageIntent.putExtra("type",message.messageType)
            messageIntent.putExtra("subject",message.subject)
            messageIntent.putExtra("body",message.body)
            startActivity(messageIntent)
        }

        composeButton!!.setOnClickListener {
            startActivity(Intent(this@MailInboxActivity,NewMessageActivity::class.java).putExtra(USER_ID,uid).putExtra(USER_EMAIL,uemail))
        }




    }

    override fun onStart(){
        super.onStart()

        databaseInbox!!.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                messageList!!.clear()

                var message: Message? = null
                for (data in snapshot.child(uid!!).children){
                    try {
                        message = data.getValue(Message::class.java)
                    }catch (e:Exception){
                        Log.e(TAG,e.toString())
                    }finally{
                        messageList!!.add(message!!)
                    }
                }


                val sortedList = messageList!!.sortedByDescending{it.date}
                messageList = sortedList.toMutableList()
                val messageAdapter = MailMessage(this@MailInboxActivity,messageList!!)
                mailListView!!.adapter = messageAdapter

            }




            override fun onCancelled(error: DatabaseError) {
                Log.i(TherapistHomeActivity.TAG, "loading mail messages was canceled")
            }
        })
    }

    companion object{
        const val USER_ID = "com.example.tesla.myhomelibrary.userid"
        const val TAG = "feelings-diary-log"
        const val USER_EMAIL = "com.example.tesla.myhomelibrary.useremail"
    }


}