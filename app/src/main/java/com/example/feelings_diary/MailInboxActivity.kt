package com.example.feelings_diary

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
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
    private var searchCriteria: String = "date"
    private var messageList2:MutableList<Message> = ArrayList()


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

        autoCompleteCriteriaSpinner!!.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (p2==0){//date
                    searchCriteria = "date"
                    var dateList = ArrayList<String>()
                    for (x in messageList!!){
                        if (!dateList.contains(x.date)){
                            dateList.add(x.date)
                        }
                    }
                    val adapter:ArrayAdapter<String> = ArrayAdapter<String>(this@MailInboxActivity,android.R.layout.simple_dropdown_item_1line,dateList)
                    autoCompleteView!!.setAdapter(adapter)
                }
                if (p2 == 1){//from
                    searchCriteria = "from"
                    var fromList = ArrayList<String>()
                    for (x in messageList!!){
                        if (!fromList.contains(x.from)){
                            fromList.add(x.from)
                        }
                    }
                    val adapter:ArrayAdapter<String> = ArrayAdapter<String>(this@MailInboxActivity,android.R.layout.simple_dropdown_item_1line,fromList)
                    autoCompleteView!!.setAdapter(adapter)


                }
                if (p2 == 2){//messagetype
                    searchCriteria = "messageType"
                    val arr = ArrayList<String>()
                    arr.add(MessageType.MESSAGE.toString())
                    arr.add(MessageType.MEETINGREQUEST.toString())
                    arr.add("message")
                    arr.add("meetingrequest")
                    val adapter:ArrayAdapter<String> = ArrayAdapter<String>(this@MailInboxActivity,android.R.layout.simple_dropdown_item_1line,arr)
                    autoCompleteView!!.setAdapter(adapter)
                }
                if(p2==3){//subject
                    searchCriteria = "subject"
                    var subjectList = ArrayList<String>()
                    for (x in messageList!!){
                        if (!subjectList.contains(x.subject)){
                            subjectList.add(x.subject)
                        }
                    }
                    val adapter:ArrayAdapter<String> = ArrayAdapter<String>(this@MailInboxActivity,android.R.layout.simple_dropdown_item_1line,subjectList)
                    autoCompleteView!!.setAdapter(adapter)
                }
                if (p2==4){//body
                    searchCriteria = "body"
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }



        searchButton!!.setOnClickListener {
            messageList2.clear()
            val criteria = autoCompleteView!!.text.toString()

            if (searchCriteria == "date"){
                for (x in messageList!!){
                    if (x.date.contains(criteria)){
                        messageList2.add(x)
                    }
                }

            }
            if (searchCriteria == "from"){
                for (x in messageList!!){
                    if (x.from.contains(criteria)){
                        messageList2.add(x)
                    }
                }

            }
            if (searchCriteria == "messageType"){
                for (x in messageList!!){
                    if (x.messageType.toString().equals(criteria.replace("\\s".toRegex(), ""),true)){
                        messageList2.add(x)
                    }
                }

            }
            if (searchCriteria == "subject"){
                for (x in messageList!!){
                    if (x.subject.contains(criteria)){
                        messageList2.add(x)
                    }
                }

            }
            if (searchCriteria == "body"){
                for (x in messageList!!){
                    if (x.body.contains(criteria)){
                        messageList2.add(x)
                    }
                }
            }
            val messageAdapter = MailMessage(this@MailInboxActivity,messageList2!!)
            mailListView!!.adapter = messageAdapter
        }



        mailListView!!.onItemClickListener = AdapterView.OnItemClickListener{ _, _, i, _ ->
            val message = messageList!![i]

            val messageIntent = Intent(this@MailInboxActivity,MailMessageActivity::class.java)
            messageIntent.putExtra(USER_ID,uid)
            messageIntent.putExtra(USER_EMAIL,uemail)
            messageIntent.putExtra("date",message.date)
            messageIntent.putExtra("from",message.from)
            messageIntent.putExtra("type",message.messageType.toString())
            messageIntent.putExtra("subject",message.subject)
            messageIntent.putExtra("body",message.body)
            if (message.meeting!= null){
                messageIntent.putExtra("meeting",message.meeting)
            }
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