@file:Suppress("CascadeIf", "CascadeIf")

package com.example.feelings_diary

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class LoginActivity : AppCompatActivity() {
    private var mDatabase: FirebaseDatabase? = null
    private var userEmail: EditText? = null
    private var userPassword: EditText? = null
    private var loginBtn: Button? = null
    private var progressBar: ProgressBar? = null
    private var userGroup:String? = null



    private var mAuth: FirebaseAuth? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        Log.i(TAG,"Entered LoginActivity")

        mDatabase = FirebaseDatabase.getInstance()
        mAuth = FirebaseAuth.getInstance()

        userEmail = findViewById(R.id.email)
        userPassword = findViewById(R.id.password)
        loginBtn = findViewById(R.id.login)
        progressBar = findViewById(R.id.progressBar)



        if (!intent.getStringExtra(USER_EMAIL).isNullOrEmpty()) {
            userEmail!!.setText(intent.getStringExtra("email"))
        }
        if (!intent.getStringExtra("password").isNullOrEmpty()) {
            userPassword!!.setText(intent.getStringExtra("password"))
        }



        loginBtn!!.setOnClickListener { loginUserAccount() }

        if (mAuth!!.currentUser != null){
            val uid = mAuth!!.currentUser!!.uid
            FirebaseDatabase.getInstance().getReference("users").addListenerForSingleValueEvent(object:ValueEventListener{

                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.i(TAG,"this was executed")
                    val user: User? = snapshot.child(uid).getValue(User::class.java)
                    userGroup = user!!.group
                    if(userGroup.equals("therapist",true)){
                        startActivity(Intent(this@LoginActivity,TherapistHomeActivity::class.java).putExtra(USER_ID,
                            mAuth!!.currentUser!!.uid))
                    }else if (userGroup.equals("patient",true)){
                        startActivity(Intent(this@LoginActivity,PatientHomeActivity::class.java).putExtra(USER_ID,
                            mAuth!!.currentUser!!.uid))
                    }else{
                        Log.i(TAG,"User group did not match therapist or patient")
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    Log.i(TAG,"loading user group canceled")
                }
            })
        }
    }



    // TODO: Allow the user to log into their account
    // If the email and password are not empty, try to log in
    // If the login is successful, store info into intent and launch DashboardActivity
    private fun loginUserAccount() {
        progressBar!!.visibility = View.VISIBLE

        val email: String = userEmail!!.text.toString()
        val password: String = userPassword!!.text.toString()

        if(email.isEmpty() || password.isEmpty()) {
            Toast.makeText(
                applicationContext,
                "Please enter valid email and password",
                Toast.LENGTH_LONG
            ).show()

            return
        }



        mAuth!!.signInWithEmailAndPassword(email,password).addOnCompleteListener{task ->
            progressBar!!.visibility = View.GONE
            if (task.isSuccessful){
                Toast.makeText(applicationContext,"Login Successful",Toast.LENGTH_LONG).show()
                val uid = mAuth!!.currentUser!!.uid
                Log.i(TAG,"uid = $uid")


                    FirebaseDatabase.getInstance().getReference("users").addListenerForSingleValueEvent(object:ValueEventListener{

                    override fun onDataChange(snapshot: DataSnapshot) {
                        Log.i(TAG,"this was executed")
                        val user: User? = snapshot.child(uid).getValue(User::class.java)
                        userGroup = user!!.group
                        if(userGroup.equals("therapist",true)){
                            startActivity(Intent(this@LoginActivity,TherapistHomeActivity::class.java).putExtra(USER_ID,
                                mAuth!!.currentUser!!.uid).putExtra(USER_EMAIL,mAuth!!.currentUser!!.email))
                        }else if (userGroup.equals("patient",true)){
                            startActivity(Intent(this@LoginActivity,PatientHomeActivity::class.java).putExtra(USER_ID,
                                mAuth!!.currentUser!!.uid).putExtra(USER_EMAIL,mAuth!!.currentUser!!.email))
                        }else{
                            Log.i(TAG,"User group did not match therapist or patient")
                        }

                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.i(TAG,"loading user group canceled")
                    }
                })


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