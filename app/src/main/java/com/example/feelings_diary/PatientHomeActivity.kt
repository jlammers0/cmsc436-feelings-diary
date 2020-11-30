package com.example.feelings_diary

import android.os.Bundle
import android.widget.Button
import android.widget.CalendarView
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class PatientHomeActivity : AppCompatActivity() {

    private var calendarView:CalendarView?=null
    private var checkInButton:Button?=null
    private var mailButton:ImageButton?=null
    private var calendarButton:ImageButton?=null
    private var settingsButton:ImageButton?=null
    private var logoutButton:ImageButton?=null



    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.patient_home)

        calendarView = findViewById(R.id.calendarView)
        checkInButton = findViewById(R.id.checkInButton)
        mailButton = findViewById(R.id.patientMailButton)
        calendarButton = findViewById(R.id.patientCalendarButton)
        settingsButton = findViewById(R.id.patientSettingsButton)
        logoutButton = findViewById(R.id.patientLogoutButton)


    }


}