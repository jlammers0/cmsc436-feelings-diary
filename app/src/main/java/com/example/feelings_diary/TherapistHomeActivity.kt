package com.example.feelings_diary

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase

class TherapistHomeActivity : AppCompatActivity() {

    private lateinit var checkInButton: Button
    private lateinit var messageButton: Button
    private lateinit var aptButton: Button
    private lateinit var queryButton: Button
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.therapist_home)

        Log.i(TAG,"Entered TherapistHomeActivity")

        // Init buttons
        checkInButton = findViewById(R.id.checkInButton)
        messageButton = findViewById(R.id.messageButton)
        aptButton = findViewById(R.id.aptButton)
        queryButton = findViewById(R.id.queryButton)
        mailButton = findViewById(R.id.therapistMailButton)
        calendarButton = findViewById(R.id.therapistCalendarButton)
        addPatientButton = findViewById(R.id.therapistAddPatientButton)
        settingsButton = findViewById(R.id.therapistSettingsButton)
        logoutButton = findViewById(R.id.therapistLogoutButton)
        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance()
        databasePatients = FirebaseDatabase.getInstance().getReference("patients")
        databaseProspectivePatients = FirebaseDatabase.getInstance().getReference("prospectivePatients")
        patients = ArrayList()
        prospectivePatients = ArrayList()

        uid = intent.getStringExtra(USER_ID)
        uemail = intent.getStringExtra(USER_EMAIL)

        //TODO: patientList made into listView. Onclick will only select patients and allow other buttons to act on selected patient

        checkInButton.setOnClickListener{
            //TODO: view list of this patients most recent checkins. listview
            //requires patient to be selected from patient list
        }

        messageButton.setOnClickListener{
            //TODO: use the messenger. use intents to feed in the patient and allow therapist to full subject and body. messageType=message
            //requires patient to be selected from patient list
        }

        aptButton.setOnClickListener{
            //TODO: use the messenger. use intents to feed patient. messsageType=MEETINGREQUEST. use a subject and body template
            //requires patient to be selected from patient list
        }

        queryButton.setOnClickListener{
            //TODO: allow therapist to query all their patient check-ins. will interact with firebase to find check in data
            //need to create activity for this. should have spinner to query by different criteria
            //feeling range will be an integer matching patient check in slider values
            //keyword will search through diary entry object comments
            //query by patient not necessary as this ability is already allowed in the therapist home screen
            //this will not require a patient to be selected from patient list
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
            //TODO: This code isn't finished yet. I forget where i left off
            var patientEmail: String? = null

            val dialogBuilder = AlertDialog.Builder(this)
            val inflater = layoutInflater
            val dialogView = inflater.inflate(R.layout.therapist_find_patient,null)
            dialogBuilder.setView(dialogView)


            val patientAddButton = findViewById<View>(R.id.addPatientButton) as Button
            val patientIgnoreButton = findViewById<View>(R.id.ignorePatientButton) as Button
            val prospectivePatientList = findViewById<View>(R.id.prospectivePatientList) as ListView

            val patientAdapter = ProspectivePatientList(this@TherapistHomeActivity,patients)
            prospectivePatientList.adapter = patientAdapter

            prospectivePatientList.onItemClickListener = AdapterView.OnItemClickListener{ _, _, i, _ ->
                patientEmail = prospectivePatients[i].email

            }

            patientAddButton.setOnClickListener{
                if (patientEmail.isNullOrEmpty()){
                    Toast.makeText(applicationContext,"Please select patient email from prospective patient list ",Toast.LENGTH_LONG).show()
                }else{
                    FirebaseDatabase.getInstance().getReference("users").addListenerForSingleValueEvent(object:ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            var user: User? = null
                            for (data in snapshot.children){
                                user = data.getValue(User::class.java)
                                if (user!!.email == patientEmail && user.group == "patient"){
                                    break
                                }
                            }
                            if (user == null){
                                Log.i(TAG,"User was not found")
                                Toast.makeText(applicationContext,"Requested patient was not found", Toast.LENGTH_LONG).show()
                            }else{
                                patients.add(user)
                                prospectivePatients.remove(user)
                                mDatabase!!.reference.child("prospectivePatients").child(uid!!).child(user.uid).removeValue()
                                mDatabase!!.reference.child("patients").child(uid!!).child(user.uid).setValue(user)
                            }

                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.i(TAG, "loading user group canceled")
                        }
                    })
                }
            }
            patientIgnoreButton.setOnClickListener{
                if (patientEmail.isNullOrEmpty()){
                    Toast.makeText(applicationContext,"No patient request selected",Toast.LENGTH_LONG).show()
                }else{
                    FirebaseDatabase.getInstance().getReference("users").addListenerForSingleValueEvent(object:ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            var user: User? = null
                            for (data in snapshot.children){
                                user = data.getValue(User::class.java)
                                if (user!!.email == patientEmail && user!!.group == "patient"){
                                    break
                                }
                            }
                            if (user == null){
                                Log.i(TAG,"User was not found")
                                Toast.makeText(applicationContext,"Requested patient was not found", Toast.LENGTH_LONG).show()
                            }else{
                                prospectivePatients.remove(user)
                                mDatabase!!.reference.child("prospectivePatients").child(uid!!).child(user!!.uid).removeValue()
                            }

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

            }




            override fun onCancelled(error: DatabaseError) {
                Log.i(TAG, "loading patients was canceled")
            }
        })

        databaseProspectivePatients.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                patients.clear()
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
    }
    companion object{
        const val TAG = "feelings-diary-log"
        const val USER_ID = "com.example.tesla.myhomelibrary.userid"
        const val USER_EMAIL = "com.example.tesla.myhomelibrary.useremail"
    }
}