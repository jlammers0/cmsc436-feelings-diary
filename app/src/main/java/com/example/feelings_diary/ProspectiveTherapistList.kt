package com.example.feelings_diary

import android.annotation.SuppressLint
import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class ProspectiveTherapistList (private val context: Activity, private var therapists: List<User>) : ArrayAdapter<User>(context,R.layout.prospective_therapist_list,therapists) {
    @SuppressLint("ViewHolder", "InflateParams")
    override fun getView(position:Int, convertView: View?, parent: ViewGroup): View{
        val inflater = context.layoutInflater
        val listViewItem = inflater.inflate(R.layout.prospective_therapist_list,null,true)
        val textViewTherapistEmail = listViewItem.findViewById<View>(R.id.textViewTherapistEmail) as TextView
        val textViewTherapistUsername = listViewItem.findViewById<View>(R.id.textViewTherapistUsername) as TextView
        val user = therapists[position]
        textViewTherapistEmail.text = user.email
        textViewTherapistUsername.text = user.uname
        return listViewItem
    }
}