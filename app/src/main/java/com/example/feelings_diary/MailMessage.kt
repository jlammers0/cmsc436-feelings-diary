package com.example.feelings_diary

import android.annotation.SuppressLint
import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class MailMessage (private val context: Activity, private var mail: List<Message>) : ArrayAdapter<Message>(context,R.layout.mail_view, mail) {

    @SuppressLint("ViewHolder", "InflateParams")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = context.layoutInflater
        val listViewItem = inflater.inflate(R.layout.mail_view,null,true)

        val mailDateView = listViewItem.findViewById<View>(R.id.mailDate) as TextView
        val mailFromView = listViewItem.findViewById<View>(R.id.mailFrom) as TextView
        val mailTypeView = listViewItem.findViewById<View>(R.id.mailType) as TextView
        val mailSubjectView = listViewItem.findViewById<View>(R.id.mailSubject) as TextView



        val message = mail[position]
        val mFrom = message.from
        val newmFrom = "From: $mFrom"
        val mType = message.messageType.toString()
        val newmType = "Type: $mType"
        val mSubject = message.subject
        val newmSubject = "Subject: $mSubject"
        mailDateView.text = message.date
        mailFromView.text = newmFrom
        mailTypeView.text = newmType
        mailSubjectView.text = newmSubject

        return listViewItem

    }
}