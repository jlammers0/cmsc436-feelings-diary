package com.example.feelings_diary

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {
    private var registerBtn: Button?= null
    private var loginBtn: Button?=null
    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.i(TAG,"Entered MainActivity")

        mAuth = FirebaseAuth.getInstance()

        // TODO: This is to make testing easier remove this in actual app
        mAuth!!.signOut()



        if (mAuth!!.currentUser != null){
            val uid = mAuth!!.currentUser!!.uid
            FirebaseDatabase.getInstance().getReference("users").addListenerForSingleValueEvent(object:
                ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.i(LoginActivity.TAG,"this was executed")
                    var user: User? = null
                    user = snapshot.child(uid).getValue(User::class.java)
                    val userGroup = user!!.group
                    if(userGroup.equals("therapist",true)){
                        startActivity(Intent(this@MainActivity,TherapistHomeActivity::class.java).putExtra(
                            USER_ID,
                            mAuth!!.currentUser!!.uid).putExtra(USER_EMAIL,mAuth!!.currentUser!!.email))
                    }else if (userGroup.equals("patient",true)){
                        startActivity(Intent(this@MainActivity,PatientHomeActivity::class.java).putExtra(
                            USER_ID,
                            mAuth!!.currentUser!!.uid).putExtra(USER_EMAIL,mAuth!!.currentUser!!.email))
                    }else{
                        Log.i(LoginActivity.TAG,"User group did not match therapist or patient")
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    Log.i(LoginActivity.TAG,"loading user group canceled")
                }
            })

        }
        registerBtn = findViewById(R.id.register)
        loginBtn = findViewById(R.id.login)
        registerBtn!!.setOnClickListener{
            startActivity(Intent(this@MainActivity, RegistrationActivity::class.java))
        }
        loginBtn!!.setOnClickListener{
            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
        }

    }
    companion object{
        const val TAG = "feelings-diary-log"
        const val USER_EMAIL = "com.example.tesla.myhomelibrary.useremail"
        const val USER_ID = "com.example.tesla.myhomelibrary.userid"
    }
}