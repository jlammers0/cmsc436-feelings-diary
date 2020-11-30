package com.example.feelings_diary

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.therapist_home)

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


        
    }
}