package com.example.feelings_diary

import android.annotation.SuppressLint
import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class PatientNotificationList(private val context: Activity,
                              private var notifications: List<PatientNotification>) :
    ArrayAdapter<PatientNotification>(context, R.layout.patient_settings, notifications) {

    @SuppressLint("ViewHolder", "InflateParams", "SetTextI18n")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // Get inflater and inflate listview
        val inflater = context.layoutInflater
        val listViewItem = inflater.inflate(R.layout.notification_list, null, true)

        // Get refs to item textView and current notification
        val notificationText = listViewItem.findViewById<TextView>(R.id.notificationTextView)
        val notification = notifications[position]

        // Get notificaiotn time
        val year = notification.year
        val month = notification.month
        val day = notification.day
        val hour = notification.hour
        val minute = notification.minute

        // Set textView text and return item's view
        notificationText.text = "$month/$day/$year at $hour:$minute"
        return listViewItem
    }

}