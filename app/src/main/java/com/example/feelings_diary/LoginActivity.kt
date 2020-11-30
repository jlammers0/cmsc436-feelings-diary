package com.example.feelings_diary

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class LoginActivity : AppCompatActivity() {
    private var mDatabaseReference: DatabaseReference? = null
    private var mDatabase: FirebaseDatabase? = null
    private var userEmail: EditText? = null
    private var userPassword: EditText? = null
    private var loginBtn: Button? = null
    private var progressBar: ProgressBar? = null
    private var userGroup:String? = null
    private var radioFlag = false
    private var radioGroup: RadioGroup? = null
    private var radioButtonPatient: RadioButton? = null
    private var radioButtonTherapist: RadioButton? = null

    private var mAuth: FirebaseAuth? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        Log.i(TAG,"Entered LoginActivity")

        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference.child("Users")
        mAuth = FirebaseAuth.getInstance()

        userEmail = findViewById(R.id.email)
        userPassword = findViewById(R.id.password)
        loginBtn = findViewById(R.id.login)
        progressBar = findViewById(R.id.progressBar)



        if (!intent.getStringExtra("email").isNullOrEmpty()) {
            userEmail!!.setText(intent.getStringExtra("email"))
        }
        if (!intent.getStringExtra("password").isNullOrEmpty()) {
            userPassword!!.setText(intent.getStringExtra("password"))
        }



        loginBtn!!.setOnClickListener { loginUserAccount() }
    }



    // TODO: Allow the user to log into their account
    // If the email and password are not empty, try to log in
    // If the login is successful, store info into intent and launch DashboardActivity
    private fun loginUserAccount() {
        progressBar!!.visibility = View.VISIBLE

        val email: String = userEmail!!.text.toString()
        val password: String = userPassword!!.text.toString()

        if(email.isNullOrEmpty() || password.isNullOrEmpty()) {
            Toast.makeText(
                applicationContext,
                "Please enter valid email and password",
                Toast.LENGTH_LONG
            ).show()

            return
        }
        if(!radioFlag){
            Toast.makeText(applicationContext,"Please select patient or therapist status",Toast.LENGTH_LONG).show()
            return
        }
        //mAuth!!.setTenantId(tenantID!!)

        mAuth!!.signInWithEmailAndPassword(email,password).addOnCompleteListener{task ->
            progressBar!!.visibility = View.GONE
            if (task.isSuccessful){
                Toast.makeText(applicationContext,"Login Successful",Toast.LENGTH_LONG).show()
                val uid = mAuth!!.currentUser!!.uid
                val userGroup = mDatabaseReference!!.child(uid).key

                if(userGroup.equals("therapist",true)){
                    startActivity(Intent(this@LoginActivity,TherapistHomeActivity::class.java).putExtra(USER_ID,
                        mAuth!!.currentUser!!.uid))
                }else if (userGroup.equals("patient",true)){
                    startActivity(Intent(this@LoginActivity,PatientHomeActivity::class.java).putExtra(USER_ID,
                        mAuth!!.currentUser!!.uid))
                }else{
                    Log.i(TAG,"User group did not match therapist or patient")
                }

            }else{
                Toast.makeText(applicationContext,"Login Failed",Toast.LENGTH_LONG).show()
            }
        }

    }

    companion object {
        const val USER_EMAIL = "com.example.tesla.myhomelibrary.useremail"
        const val USER_ID = "com.example.tesla.myhomelibrary.userid"
        const val TAG = "feelings-diary-log"
    }
}