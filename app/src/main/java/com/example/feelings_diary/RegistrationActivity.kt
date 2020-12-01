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

class RegistrationActivity : AppCompatActivity() {

    private var emailTV: EditText? = null
    private var passwordTV: EditText? = null
    private var regBtn: Button? = null
    private var progressBar: ProgressBar? = null
    private var validator = Validators()
    var userGroup: String? = null
    private var radioFlag = false
    private var mDatabaseReference: DatabaseReference? = null
    private var mDatabase: FirebaseDatabase? = null

    private var mAuth: FirebaseAuth? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        Log.i(TAG,"Entered RegistrationActivity")

        mAuth = FirebaseAuth.getInstance()

        emailTV = findViewById(R.id.email)
        passwordTV = findViewById(R.id.password)
        regBtn = findViewById(R.id.register)
        progressBar = findViewById(R.id.progressBar)
        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference


        regBtn!!.setOnClickListener { registerNewUser() }
    }

    fun registrationRadioClickCallback(v: View) {
        val rb = v as RadioButton

        // RadioButtons report each click, even if the toggle state doesn't change
        if (rb.isChecked) {
            userGroup = rb!!.text.toString()
            radioFlag = true
        }
    }

    private fun registerNewUser() {
        progressBar!!.visibility = View.VISIBLE

        val email: String = emailTV!!.text.toString()
        val password: String = passwordTV!!.text.toString()

        if (!validator.validEmail(email)) {
            Toast.makeText(applicationContext, "Please enter a valid email...", Toast.LENGTH_LONG).show()
            return
        }
        if (!validator.validPassword(password)) {
            Toast.makeText(applicationContext, "Password must be between 6 and 16 characters and contain at least 1 letter and 1 number", Toast.LENGTH_LONG).show()
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
                        Log.i(TAG,"Uid = ${uid}")
                        val user = User(email,uid,userGroup!!)
                        mDatabaseReference!!.child("users").child(uid).setValue(user)
                        val intent = Intent(this@RegistrationActivity, LoginActivity::class.java)
                        intent.putExtra("email",email)
                        intent.putExtra("password",password)
                        startActivity(intent)
                    } else {
                        Toast.makeText(applicationContext, "Registration failed! Please try again later", Toast.LENGTH_LONG).show()
                        progressBar!!.visibility = View.GONE
                    }
                }
    }
    companion object{
        const val TAG = "feelings-diary-log"
    }

}
