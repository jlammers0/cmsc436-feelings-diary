package com.example.feelings_diary

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.*

/** Activity for therapist home **/
class TherapistHomeActivity : AppCompatActivity() {

    // UI Buttons
    private lateinit var checkInButton: Button
    private lateinit var messageButton: Button
    private lateinit var aptButton: Button
    private lateinit var queryButton: Button

    // Patient list
    private lateinit var patientList: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.therapist_home)

        // Init buttons
        checkInButton = findViewById(R.id.checkInButton)
        messageButton = findViewById(R.id.messageButton)
        aptButton = findViewById(R.id.aptButton)
        queryButton = findViewById(R.id.queryButton)

        // Init list
        patientList = findViewById(R.id.patientList)
        patientList.adapter = PatientAdapter()

        // TODO implement firebase support to get patient data
    }


    /** List adapter for patient list view **/
    inner class PatientAdapter : BaseAdapter() {

        private val patients = ArrayList<String>()

        fun add(patient: String) {
            patients.add(patient)
        }

        override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
            val patient = getItem(p0) as String

            val patientView = TextView(applicationContext)
            patientView.text = patient
            patientView.setOnClickListener {
                // TODO Change this intent to launch the patient view activity
                val patientIntent = Intent(applicationContext, MainActivity::class.java)
                patientIntent.putExtra("PatientName", patient)

                startActivity(patientIntent)
            }

            return patientView
        }

        override fun getItem(p0: Int): Any {
            return patients[p0]
        }

        override fun getItemId(p0: Int): Long {
            return p0.toLong()
        }

        override fun getCount(): Int {
            return patients.size
        }

    }
}