package com.example.feelings_diary

import android.content.ContentUris
import android.content.Intent
import android.os.Bundle
import android.provider.CalendarContract
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class TherapistHomeActivity : AppCompatActivity() {

    private lateinit var checkInButton: Button
    private lateinit var patientListView: ListView
    private lateinit var messageButton: Button
    private lateinit var aptButton: Button

    private lateinit var mailButton: ImageButton
    private lateinit var calendarButton: ImageButton
    private lateinit var addPatientButton: ImageButton
    private lateinit var settingsButton: ImageButton
    private lateinit var logoutButton: ImageButton
    private var mAuth: FirebaseAuth? = null
    private var mDatabase: FirebaseDatabase? = null
    private lateinit var databasePatients: DatabaseReference
    private lateinit var databaseProspectivePatients: DatabaseReference
    private var uid:String? = null
    private var uemail:String? = null
    private lateinit var patients: MutableList<User>
    private lateinit var prospectivePatients: MutableList<User>
    private var selectedPatient: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.therapist_home)

        Log.i(TAG,"Entered TherapistHomeActivity")

        // Init buttons
        checkInButton = findViewById(R.id.checkInButton)
        messageButton = findViewById(R.id.messageButton)
        aptButton = findViewById(R.id.aptButton)

        mailButton = findViewById(R.id.therapistMailButton)
        calendarButton = findViewById(R.id.therapistCalendarButton)
        addPatientButton = findViewById(R.id.therapistAddPatientButton)
        settingsButton = findViewById(R.id.therapistSettingsButton)
        logoutButton = findViewById(R.id.therapistLogoutButton)

        mAuth = FirebaseAuth.getInstance()
        patientListView = findViewById(R.id.patientList)
        mDatabase = FirebaseDatabase.getInstance()
        //database patients root -> patients -> therapist uid -> patient uid -> patient user object
        databasePatients = FirebaseDatabase.getInstance().getReference("patients")
        //database prospectivePatients root -> prospectivePatients -> therapist uid -> patient uid -> patient user object
        databaseProspectivePatients = FirebaseDatabase.getInstance().getReference("prospectivePatients")
        patients = ArrayList()
        prospectivePatients = ArrayList()

        uid = intent.getStringExtra(USER_ID)
        uemail = intent.getStringExtra(USER_EMAIL)

        //TODO: patientList made into listView. Onclick will only select patients and allow other buttons to act on selected patient


        patientListView.onItemClickListener = AdapterView.OnItemClickListener{_,_,i,_->
            selectedPatient = patients[i]
        }

        calendarButton.setOnClickListener{
            val calendarEventTime = System.currentTimeMillis()

            val builder = CalendarContract.CONTENT_URI.buildUpon().appendPath("time")
            ContentUris.appendId(builder,calendarEventTime)
            val uri = builder.build()
            val intent = Intent(Intent.ACTION_VIEW).setData(uri)
            startActivity(intent)
        }

        checkInButton.setOnClickListener{
            //TODO: view list of this patients most recent checkins. listview
            //requires patient to be selected from patient list
            if (selectedPatient == null){
                Toast.makeText(applicationContext,"Must select a patient from list to view their check-ins",Toast.LENGTH_SHORT).show()
            }else{
                startActivity(Intent(this@TherapistHomeActivity,TCICommentActivity::class.java).putExtra(
                    USER_ID, uid).putExtra(
                    USER_EMAIL,uemail).putExtra(
                    CURR_PATIENT_ID, selectedPatient!!.uid).putExtra(
                    CURR_PATIENT_EMAIL, selectedPatient!!.email)
                )
            }
        }

        settingsButton.setOnClickListener{
            var curPatient:User? = null
            val dialogBuilder = AlertDialog.Builder(this)
            val inflater = layoutInflater
            val dialogView = inflater.inflate(R.layout.therapist_settings,null)
            dialogBuilder.setView(dialogView)

            val removePatientButton = dialogView.findViewById<View>(R.id.removePatientButton) as Button

            val subscribedPatientList = dialogView.findViewById<View>(R.id.subscribedPatientList) as ListView

            val subscribedPatientListAdapter = ProspectivePatientList(this,patients)
            subscribedPatientList.adapter = subscribedPatientListAdapter

            subscribedPatientList.onItemClickListener = AdapterView.OnItemClickListener{_,_,i,_ ->
                curPatient = patients[i]
            }

            dialogBuilder.setTitle("Therapist Settings")
            val b = dialogBuilder.create()
            b.show()
            removePatientButton.setOnClickListener{
                val x = patients.remove(curPatient)
                if (x) {
                    mDatabase!!.reference.child("patients").child(uid!!).child(curPatient!!.uid)
                        .removeValue()
                    Toast.makeText(this, "Patient has been removed", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this,"You need to select a patient to remove",Toast.LENGTH_SHORT).show()
                }
                b.dismiss()
            }



        }

        messageButton.setOnClickListener{
            //TODO: use the messenger. use intents to feed in the patient and allow therapist to full subject and body. messageType=message
            //requires patient to be selected from patient list
            if (selectedPatient == null){
                Toast.makeText(applicationContext,"Must select a patient from list to send a message",Toast.LENGTH_SHORT).show()
            }else{
                val messageIntent = Intent(this@TherapistHomeActivity, NewMessageActivity::class.java)
                messageIntent.putExtra(USER_ID,uid)
                messageIntent.putExtra(USER_EMAIL,uemail)
                messageIntent.putExtra("TO",selectedPatient!!.email)
                startActivity(messageIntent)
            }

        }

        aptButton.setOnClickListener{
            //TODO: use the messenger. use intents to feed patient. messsageType=MEETINGREQUEST. use a subject and body template
            //requires patient to be selected from patient list

            if (selectedPatient == null){
                Toast.makeText(applicationContext,"Must select a patient from list to send a message",Toast.LENGTH_SHORT).show()
            }else{
                val messageIntent = Intent(this@TherapistHomeActivity, NewMessageActivity::class.java)
                messageIntent.putExtra(USER_ID,uid)
                messageIntent.putExtra(USER_EMAIL,uemail)
                messageIntent.putExtra("TO",selectedPatient!!.email)

                messageIntent.putExtra("SUBJECT","Requesting a Meeting")
                startActivity(messageIntent)
            }

        }



        logoutButton.setOnClickListener{
            mAuth!!.signOut()
            Toast.makeText(applicationContext,"You have been successfully logged out",Toast.LENGTH_LONG).show()
            startActivity(Intent(this@TherapistHomeActivity,MainActivity::class.java))
        }

        mailButton.setOnClickListener{
            startActivity(Intent(this@TherapistHomeActivity,MailInboxActivity::class.java).putExtra(USER_ID,uid).putExtra(
                USER_EMAIL,uemail))
        }


        addPatientButton.setOnClickListener{

            for(patient in prospectivePatients){
                Log.i(TAG,"patients contains ${patient.email}")
            }
            //TODO: This code isn't finished yet. I forget where i left off
            var patient: User? = null

            val dialogBuilder = AlertDialog.Builder(this)
            val inflater = layoutInflater
            val dialogView = inflater.inflate(R.layout.therapist_find_patient,null)
            dialogBuilder.setView(dialogView)


            val patientAddButton = dialogView.findViewById<View>(R.id.addPatientButton) as Button
            val patientIgnoreButton = dialogView.findViewById<View>(R.id.ignorePatientButton) as Button
            val prospectivePatientList = dialogView.findViewById<View>(R.id.prospectivePatientList) as ListView

            dialogBuilder.setTitle("Add a Patient")
            val b = dialogBuilder.create()


            val patientAdapter = ProspectivePatientList(this@TherapistHomeActivity,prospectivePatients)
            prospectivePatientList.adapter = patientAdapter

            b.show()
            for(patient in prospectivePatients){
                Log.i(TAG,"patients contains ${patient.email}")
            }

            prospectivePatientList.onItemClickListener = AdapterView.OnItemClickListener{ _, _, i, _ ->
                patient = prospectivePatients[i]

            }

            patientAddButton.setOnClickListener{
                if (patient == null){
                    Toast.makeText(applicationContext,"Please select patient email from prospective patient list ",Toast.LENGTH_LONG).show()
                }else{
                    FirebaseDatabase.getInstance().getReference("prospectivePatients").addListenerForSingleValueEvent(object:ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            var user: User? = null
                            var exists = false
                            for (data in snapshot.child(uid!!).children){
                                user = data.getValue(User::class.java)
                                if (user!!.email == patient!!.email){
                                    exists = true

                                }
                            }
                            if (!exists || patient == null){
                                Log.i(TAG,"User was not found")
                                Toast.makeText(applicationContext,"Requested patient was not found", Toast.LENGTH_LONG).show()

                            }else{
                                patients.add(patient!!)
                                prospectivePatients.remove(patient)
                                mDatabase!!.reference.child("prospectivePatients").child(uid!!).child(patient!!.uid).removeValue()
                                mDatabase!!.reference.child("patients").child(uid!!).child(patient!!.uid).setValue(patient!!)
                            }
                             b.dismiss()
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.i(TAG, "loading user group canceled")
                        }
                    })
                }
            }
            patientIgnoreButton.setOnClickListener{
                if (patient == null){
                    Toast.makeText(applicationContext,"No patient request selected",Toast.LENGTH_LONG).show()
                }else{
                    FirebaseDatabase.getInstance().getReference("prospectivePatients").addListenerForSingleValueEvent(object:ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            var user: User? = null
                            var existsFlag = false
                            for (data in snapshot.child(uid!!).children){
                                user = data.getValue(User::class.java)
                                if (user!!.email == patient!!.email ){
                                    existsFlag = true
                                }
                            }
                            if (!existsFlag ||patient== null){
                                Log.i(TAG,"User was not found")
                                Toast.makeText(applicationContext,"Requested patient was not found", Toast.LENGTH_LONG).show()
                            }else{
                                prospectivePatients.remove(user)
                                mDatabase!!.reference.child("prospectivePatients").child(uid!!).child(patient!!.uid).removeValue()
                            }
                            b.dismiss()

                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.i(TAG, "loading user group canceled")
                        }
                    })
                }
            }



        }


        
    }

    override fun onStart(){
        super.onStart()

        databasePatients.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                patients.clear()
                var user: User? = null
                for (data in snapshot.child(uid!!).children){
                    try {
                        user = data.getValue(User::class.java)
                    }catch (e:Exception){
                        Log.e(TAG,e.toString())
                    }finally{
                        patients.add(user!!)
                    }
                }
                val patientListAdapter = ProspectivePatientList(this@TherapistHomeActivity,patients)
                patientListView.adapter = patientListAdapter

            }




            override fun onCancelled(error: DatabaseError) {
                Log.i(TAG, "loading patients was canceled")
            }
        })

        databaseProspectivePatients.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                prospectivePatients.clear()
                var user: User? = null
                for (data in snapshot.child(uid!!).children){
                    try {
                        user = data.getValue(User::class.java)
                    }catch (e:Exception){
                        Log.e(TAG,e.toString())
                    }finally{
                        prospectivePatients.add(user!!)
                    }
                }

            }




            override fun onCancelled(error: DatabaseError) {
                Log.i(TAG, "loading patients was canceled")
            }
        })

        FirebaseDatabase.getInstance().getReference("patients").addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                var user: User? = null
                for (data in snapshot.children){
                    for (x in data.children){
                        try{
                            user = x.getValue(User::class.java)
                        }catch (e:Exception){
                            Log.e(TAG,e.toString())
                        }finally{
                            for (y in prospectivePatients){
                                if(user!!.uid == y.uid){
                                    prospectivePatients.remove(user)
                                    mDatabase!!.reference.child("prospectivePatients").child(uid!!).child(user!!.uid).removeValue()
                                }
                            }
                        }
                    }
                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
    companion object{
        const val TAG = "feelings-diary-log"
        const val USER_ID = "com.example.tesla.myhomelibrary.userid"
        const val USER_EMAIL = "com.example.tesla.myhomelibrary.useremail"
        const val CURR_PATIENT_ID = "CPID"
        const val CURR_PATIENT_EMAIL = "CPEM"
    }
}