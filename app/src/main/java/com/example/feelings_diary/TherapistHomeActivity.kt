package com.example.feelings_diary

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
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
    private var uid:String? = null
    private lateinit var patients: MutableList<User>

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
        patients = ArrayList()

        uid = intent.getStringExtra(USER_ID)

        logoutButton.setOnClickListener{
            mAuth!!.signOut()
            Toast.makeText(applicationContext,"You have been successfully logged out",Toast.LENGTH_LONG).show()
            startActivity(Intent(this@TherapistHomeActivity,LoginActivity::class.java))
        }

        addPatientButton.setOnClickListener{
            val patientEmail: String? = null

            val dialogBuilder = AlertDialog.Builder(this)
            val inflater = layoutInflater
            val dialogView = inflater.inflate(R.layout.therapist_find_patient,null)
            dialogBuilder.setView(dialogView)

            val patientEmailView = findViewById<View>(R.id.patientEmail)
            val patientAddButton = findViewById<View>(R.id.addPatientButton)





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
                        patients.add(user)
                        mDatabase!!.reference!!.child("patients").child(uid!!).child(user.uid).setValue(user)
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    Log.i(TAG, "loading user group canceled")
                }
            })
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
    }
    companion object{
        const val TAG = "feelings-diary-log"
        const val USER_ID = "com.example.tesla.myhomelibrary.userid"
    }
}