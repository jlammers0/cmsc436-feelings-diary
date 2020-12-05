package com.example.feelings_diary

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CalendarView
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

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

            //TODO: build therapist list and add node to firebase connecting patients and therapists
        }

        mailButton!!.setOnClickListener{
            startActivity(Intent(this@PatientHomeActivity,MailInboxActivity::class.java).putExtra(USER_ID,uid).putExtra(
                USER_EMAIL,uemail))

        }

    }



    companion object{
        const val TAG = "feelings-diary-log"
        const val USER_ID = "com.example.tesla.myhomelibrary.userid"
        const val USER_EMAIL = "com.example.tesla.myhomelibrary.useremail"

    }


}