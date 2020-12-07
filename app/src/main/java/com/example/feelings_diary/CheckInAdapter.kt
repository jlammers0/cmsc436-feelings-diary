package com.example.feelings_diary

import android.annotation.SuppressLint
import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class CheckInAdapter (private val context: Activity, private var check: List<DiaryEntry>):
    ArrayAdapter<DiaryEntry> (context, R.layout.layout_checkin_list, check){
    @SuppressLint("ViewHolder", "InflateParams")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = context.layoutInflater
        val listViewItem = inflater.inflate(R.layout.layout_checkin_list,null,true)

        val checkInText = listViewItem.findViewById<View>(R.id.comment_text) as TextView
        val dateText = listViewItem.findViewById<View>(R.id.date_text) as TextView

        val entry = check[position]
        checkInText.text = entry.comment
        dateText.text = entry.date

        return listViewItem

    }
}