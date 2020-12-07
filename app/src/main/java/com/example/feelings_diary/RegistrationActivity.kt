package com.example.feelings_diary

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.*

class RegistrationActivity : AppCompatActivity() {

    private var emailTV: EditText? = null
    private var passwordTV: EditText? = null
    private var usernameTV: EditText? = null
    private var regBtn: Button? = null
    private var progressBar: ProgressBar? = null
    private var validator = Validators()
    var userGroup: String? = null
    private var radioFlag = false
    private var mDatabaseReference: DatabaseReference? = null
    private var mDatabase: FirebaseDatabase? = null
    private var unameList:MutableList<String>? = null

    private var mAuth: FirebaseAuth? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        Log.i(TAG,"Entered RegistrationActivity")

        mAuth = FirebaseAuth.getInstance()

        emailTV = findViewById(R.id.email)
        passwordTV = findViewById(R.id.password)
        usernameTV = findViewById(R.id.username)
        regBtn = findViewById(R.id.register)
        progressBar = findViewById(R.id.progressBar)
        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference
        unameList = ArrayList()


        regBtn!!.setOnClickListener { registerNewUser() }

        if (mAuth!!.currentUser != null){
            val uid = mAuth!!.currentUser!!.uid
            FirebaseDatabase.getInstance().getReference("users").addListenerForSingleValueEvent(object:
                ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {

                    var user: User? = null
                    user = snapshot.child(uid).getValue(User::class.java)
                    userGroup = user!!.group
                    if(userGroup.equals("therapist",true)){
                        startActivity(Intent(this@RegistrationActivity,TherapistHomeActivity::class.java).putExtra(
                            LoginActivity.USER_ID,
                            mAuth!!.currentUser!!.uid).putExtra(USER_EMAIL,mAuth!!.currentUser!!.email))
                    }else if (userGroup.equals("patient",true)){
                        startActivity(Intent(this@RegistrationActivity,PatientHomeActivity::class.java).putExtra(
                            LoginActivity.USER_ID,
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
    }

    fun registrationRadioClickCallback(v: View) {
        val rb = v as RadioButton

        // RadioButtons report each click, even if the toggle state doesn't change
        if (rb.isChecked) {
            userGroup = rb.text.toString()
            radioFlag = true
        }
    }

    private fun registerNewUser() {
        progressBar!!.visibility = View.VISIBLE

        val email: String = emailTV!!.text.toString()
        val password: String = passwordTV!!.text.toString()
        val username: String = usernameTV!!.text.toString()

        if (!validator.validEmail(email)) {
            Toast.makeText(applicationContext, "Please enter a valid email...", Toast.LENGTH_LONG).show()
            return
        }
        if (!validator.validPassword(password)) {
            Toast.makeText(applicationContext, "Password must be between 6 and 16 characters and contain at least 1 letter and 1 number", Toast.LENGTH_LONG).show()
            return
        }
        if (!validator.validUsername(username,unameList!!)&&username.length in 4..16){
            Toast.makeText(applicationContext,"This username has already been taken",Toast.LENGTH_LONG).show()
            return
        }else if (!validator.validUsername(username,unameList!!)){
            Toast.makeText(applicationContext, "Username must be between 4 and 16 characters and must be unique",Toast.LENGTH_LONG).show()
            return

        }

        if(!radioFlag){
            Toast.makeText(applicationContext,"Please identify as either a patient or therapist",Toast.LENGTH_LONG).show()
            return
        }
        //this doesnt work for user groups -- ignore
        //mAuth!!.setTenantId(tenantID!!)

        mAuth!!.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(applicationContext, "Registration successful!", Toast.LENGTH_LONG).show()
                        Log.i(TAG,"Registration successful")
                        progressBar!!.visibility = View.GONE
                        val uid = mAuth!!.currentUser!!.uid
                        Log.i(TAG,"Uid = $uid")
                        val user = User(username,email,uid,userGroup!!)
                        mDatabaseReference!!.child("users").child(uid).setValue(user)

                        //Welcome message and initializing inbox to test message system
                        if (user.group.equals("patient",true)) {
                            mDatabaseReference!!.child("inbox").child(uid).child(Date(System.currentTimeMillis()).toString()).setValue(
                                Message(
                                    Date(System.currentTimeMillis()).toString(),
                                    "Feelings Diary Team",
                                    email,
                                    MessageType.MESSAGE,
                                    "Welcome to Feelings Diary!",
                                    "Thank you for trying out our App! " +
                                            "You can use this as a standalone program to log your feelings " +
                                            "or link your account to a therapist to unlock the full" +
                                            " potential of our app! ${System.lineSeparator()} Best Wishes, ${System.lineSeparator()} Feelings Diary Team"
                                )
                            )
                        }else if (user.group.equals("therapist",true)){
                            mDatabaseReference!!.child("inbox").child(uid).child(Date(System.currentTimeMillis()).toString()).setValue(
                                Message(
                                    Date(System.currentTimeMillis()).toString(),
                                    "Feelings Diary Team",
                                    email,
                                    MessageType.MESSAGE,
                                    "Welcome to Feelings Diary!",
                                    "Thank you for trying out our App! " +
                                            "You can link your account to a patient to start staying up to date on their " +
                                            "feelings check-ins, schedule meetings, send messages, and more!" +
                                            "${System.lineSeparator()} Best Wishes, ${System.lineSeparator()} Feelings Diary Team"
                                )
                            )
                            mDatabaseReference!!.child("therapists").child(uid).setValue(user)
                        }


                        val intent = Intent(this@RegistrationActivity, LoginActivity::class.java)
                        intent.putExtra(USER_EMAIL,email)
                        intent.putExtra("password",password)

                        startActivity(intent)
                    } else {
                        Toast.makeText(applicationContext, "Registration failed! Please try again later", Toast.LENGTH_LONG).show()
                        progressBar!!.visibility = View.GONE
                    }
                }
    }
    override fun onStart(){
        super.onStart()


        FirebaseDatabase.getInstance().getReference("users")
            .addListenerForSingleValueEvent(object :
                ValueEventListener {


                override fun onDataChange(snapshot: DataSnapshot) {
                    unameList!!.clear()
                    for (data in snapshot.children) {
                        var user: User? = null
                        try {
                            user = data.getValue(User::class.java)
                        } catch (e: Exception) {
                            Log.e(MailInboxActivity.TAG, e.toString())
                        } finally {
                            unameList!!.add(user!!.uname)
                        }
                    }


                }

                override fun onCancelled(error: DatabaseError) {
                    Log.i(LoginActivity.TAG, "loading user group canceled")
                }
            })


    }
    companion object{
        const val TAG = "feelings-diary-log"
        const val USER_EMAIL = "com.example.tesla.myhomelibrary.useremail"
        const val USER_ID = "com.example.tesla.myhomelibrary.userid"
    }

}
