package com.example.feelings_diary

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class TherapistHomeActivity : AppCompatActivity() {

    private lateinit var checkInButton: Button
    private lateinit var messageButton: Button
    private lateinit var aptButton: Button
    private lateinit var queryButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.therapist_home)

        // Init buttons
        checkInButton = findViewById(R.id.checkInButton)
        messageButton = findViewById(R.id.messageButton)
        aptButton = findViewById(R.id.aptButton)
        queryButton = findViewById(R.id.queryButton)

        
    }
}