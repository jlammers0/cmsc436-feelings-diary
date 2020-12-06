package com.example.feelings_diary

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class PatientHomeActivity : AppCompatActivity() {

    private var calendarView:CalendarView?=null
    private var checkInButton:Button?=null
    private var mailButton:ImageButton?=null
    private var calendarButton:ImageButton?=null
    private var settingsButton:ImageButton?=null
    private var patientAddTherapistButton: ImageButton?= null
    private var logoutButton:ImageButton?=null
    private var mAuth: FirebaseAuth? = null
    private var mDatabase: FirebaseDatabase? = null
    private var uid:String? = null
    private var uemail:String? = null
    private var therapistList:MutableList<User>? = null
    private var databaseTherapists: DatabaseReference? = null
    private var curUser: User? = null

    private var therapistAdapter: ArrayAdapter<User>? = null




    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.patient_home)
        Log.i(TAG,"Entered PatientHomeActivity")

        calendarView = findViewById(R.id.calendarView)
        checkInButton = findViewById(R.id.patientCheckInButton)
        mailButton = findViewById(R.id.patientMailButton)
        calendarButton = findViewById(R.id.patientCalendarButton)
        settingsButton = findViewById(R.id.patientSettingsButton)
        logoutButton = findViewById(R.id.patientLogoutButton)
        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance()
        patientAddTherapistButton = findViewById(R.id.patientAddTherapistButton)
        uid = intent.getStringExtra(USER_ID)
        uemail = intent.getStringExtra(USER_EMAIL)
        FirebaseDatabase.getInstance().getReference("users").addListenerForSingleValueEvent(object:
            ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                for (data in snapshot.children){
                    val temp = data.getValue(User::class.java)
                    if (temp!!.email == uemail!!){
                        curUser=temp
                        break
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.i(TAG,"fetching users failed")
            }

        })


        therapistList = ArrayList()
        databaseTherapists = FirebaseDatabase.getInstance().getReference("therapists")


        checkInButton!!.setOnClickListener {
            val dialogBuilder = AlertDialog.Builder(this)
            val inflater = layoutInflater
            val dialogView = inflater.inflate(R.layout.patient_check_in,null)
            dialogBuilder.setView(dialogView)
            //TODO: patient_check_in is a template. Check in still needs more work
            //needs extra faces, attach slider to faces with corresponding ints
            //firebase node "diary" to be added to store patient check ins

        }

        //TODO: calendarView
        //get selected date and create a list of check-ins on that date
        //this view has not been created yet. dialog or activity with a listview similar
        //to patientList or messages will do

        settingsButton!!.setOnClickListener{
            //TODO: create reminders to check in and option to delete account
            //patient settings will no longer add therapist we have add therapist button for this
            //patient settings will need to be able to show and remove connected therapist
        }

        calendarButton!!.setOnClickListener{
            //TODO: link to android calendar https://itnext.io/android-calendar-intent-8536232ecb38
        }


        logoutButton!!.setOnClickListener{
            mAuth!!.signOut()
            Toast.makeText(applicationContext,"You have been successfully logged out", Toast.LENGTH_LONG).show()
            startActivity(Intent(this@PatientHomeActivity,MainActivity::class.java))
        }

        patientAddTherapistButton!!.setOnClickListener{

            val dialogBuilder = AlertDialog.Builder(this)
            val inflater = layoutInflater
            val dialogView = inflater.inflate(R.layout.patient_find_therapist,null)
            dialogBuilder.setView(dialogView)

            val enrolledTherapistListView = dialogView.findViewById<View>(R.id.enrolledTherapistList) as ListView
            val findTherapistByEmail = dialogView.findViewById<View>(R.id.findTherapistByEmail) as EditText
            val requestTherapistButton = dialogView.findViewById<View>(R.id.requestTherapistButton) as Button



            therapistAdapter = ProspectiveTherapistList(this,
                therapistList as ArrayList<User>
            )
            enrolledTherapistListView.adapter = therapistAdapter

            dialogBuilder.setTitle("Find therapist")
            val b = dialogBuilder.create()

            b.show()
            for(therapist in therapistList as ArrayList<User>) {
                Log.i(TAG, "therapistList contains ${therapist.email}")
            }

            //not sure why this cast is needed. might have something to do with being set to null at first
            //and not declared as lateinit
            enrolledTherapistListView.onItemClickListener = AdapterView.OnItemClickListener{_,_,i,_ ->
                findTherapistByEmail.setText(therapistList!![i].email)

            }




            requestTherapistButton.setOnClickListener {




                if (findTherapistByEmail.text.toString().isNullOrEmpty()){
                    Toast.makeText(applicationContext,"Select a therapist from the list or enter therapist email manually",Toast.LENGTH_LONG).show()
                }
                var existsFlag = false;
                for (therapist in therapistList as ArrayList<User>){
                    if (therapist.email == findTherapistByEmail.text.toString()){
                        existsFlag =true
                        FirebaseDatabase.getInstance().reference.child("prospectivePatients").child(therapist.uid).child(uid!!).setValue(curUser)
                    }
                }
                if (existsFlag){
                    Toast.makeText(applicationContext,"Therapist has been sent a patient request",Toast.LENGTH_LONG).show()
                }else{
                    Toast.makeText(applicationContext,"Therapist was not found",Toast.LENGTH_LONG).show()
                }

                //TODO request therapist

                b.dismiss()

            }

            //TODO: build therapist list and add node to firebase connecting patients and therapists
        }

        mailButton!!.setOnClickListener{
            startActivity(Intent(this@PatientHomeActivity,MailInboxActivity::class.java).putExtra(USER_ID,uid).putExtra(
                USER_EMAIL,uemail))

        }

    }

    override fun onStart() {
        super.onStart()
        databaseTherapists!!.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                therapistList!!.clear()
                var user: User? = null
                for (data in snapshot.children){
                    try {
                        user = data.getValue(User::class.java)
                    }catch (e:Exception){
                        Log.e(TAG,e.toString())
                    }finally{
                        therapistList!!.add(user!!)
                    }
                }



            }




            override fun onCancelled(error: DatabaseError) {
                Log.i(TherapistHomeActivity.TAG, "loading patients was canceled")
            }
        })
    }







    companion object{
        const val TAG = "feelings-diary-log"
        const val USER_ID = "com.example.tesla.myhomelibrary.userid"
        const val USER_EMAIL = "com.example.tesla.myhomelibrary.useremail"

    }


}