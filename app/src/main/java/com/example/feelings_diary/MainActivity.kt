package com.example.feelings_diary

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {
    private var registerBtn: Button?= null
    private var loginBtn: Button?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        registerBtn = findViewById(R.id.register)
        loginBtn = findViewById(R.id.login)
        registerBtn!!.setOnClickListener{
            startActivity(Intent(this@MainActivity, RegistrationActivity::class.java))
        }
        loginBtn!!.setOnClickListener{
            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
        }

    }
}