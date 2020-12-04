package com.example.feelings_diary

import android.os.Bundle
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.ListView
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity

    private var autoCompleteView: AutoCompleteTextView? = null
    private var autoCompleteCriteriaSpinner: Spinner? = null
    private var searchButton: Button? = null
    private var mailListView: ListView? = null


class MailInboxActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.inbox_view)

        autoCompleteView = findViewById(R.id.autoCompleteView)
        autoCompleteCriteriaSpinner = findViewById(R.id.autoCompleteCriteria)
        searchButton = findViewById(R.id.searchButton)
        mailListView = findViewById(R.id.listViewMail)



    }


}