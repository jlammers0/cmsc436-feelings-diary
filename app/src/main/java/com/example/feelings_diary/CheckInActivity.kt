package com.example.feelings_diary

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.*

class CheckInActivity: AppCompatActivity() {
    private var mFeelImg: ImageView? = null
    private var mFeelText: EditText? = null
    private var cancelButton: Button? = null
    private var saveButton: Button? = null
    private var mAuth: FirebaseAuth? = null
    private var mDatabase: FirebaseDatabase? = null
    private var mDatabaseReference: DatabaseReference? = null
    private var uid: String? = null
    private var uemail:String? = null


    private fun updateFace(rating: Int){
        when {
            rating < 10 -> mFeelImg?.setImageResource(R.drawable.angry_emoji)
            rating in 10..25 -> mFeelImg?.setImageResource(R.drawable.very_sad_emoji)
            rating in 26..41 -> mFeelImg?.setImageResource(R.drawable.unhappy_emoji)
            rating in 42..57 -> mFeelImg?.setImageResource(R.drawable.neutral_emoji)
            rating in 58..73 -> mFeelImg?.setImageResource(R.drawable.slightly_smiling_emoji)
            rating in 74..89 -> mFeelImg?.setImageResource(R.drawable.laughing_emoji)
            rating >= 90 -> mFeelImg?.setImageResource(R.drawable.very_happy_emoji)
            else -> Log.i(LoginActivity.TAG,"Invalid slider value... somehow")
        }
    }
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.patient_check_in)
        val mSeekBar = findViewById<SeekBar>(R.id.patientFeelingsSeekBar)
        mFeelImg = findViewById(R.id.patientFeelingsImage)
        mFeelText = findViewById(R.id.patientCheckInComment)
        cancelButton = findViewById(R.id.cancelCheckinButton)
        saveButton = findViewById(R.id.completeCheckinButton)
        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance()

        uid = intent.getStringExtra(USER_ID)
        uemail = intent.getStringExtra(USER_EMAIL)


        updateFace(mSeekBar.progress)

        mSeekBar?.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                updateFace(progress)
            }
            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) {}
        })

        cancelButton!!.setOnClickListener{
            Toast.makeText(applicationContext, "Check in cancelled", Toast.LENGTH_SHORT).show()
            startActivity(
                Intent(this@CheckInActivity, PatientHomeActivity::class.java).putExtra(
                    USER_ID, uid).putExtra(
                    USER_EMAIL, uemail)
            )
        }

        saveButton!!.setOnClickListener{
            val entry = DiaryEntry(Date(System.currentTimeMillis()).toString(), mSeekBar.progress, mFeelText?.text.toString())
           
        }
    }
    companion object{
        const val TAG = "feelings-diary-log"
        const val USER_ID = "com.example.tesla.myhomelibrary.userid"
        const val USER_EMAIL = "com.example.tesla.myhomelibrary.useremail"
    }
}