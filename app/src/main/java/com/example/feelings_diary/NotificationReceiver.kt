package com.example.feelings_diary

import android.app.Notification
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.ListView

class NotificationReceiver() : BroadcastReceiver() {

    companion object {
        const val NOT_ID = "feelings-diary-notification-id"
        const val NOT = "feelings-diary-notification"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        // Get ref to notification manager
        val manager = context!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Get notification and id from intent
        val notification = intent!!.getParcelableExtra<Notification>(NOT)
        val notID = intent.getIntExtra(NOT_ID, 0)

        // Send the notification
        manager.notify(notID, notification)
    }
}