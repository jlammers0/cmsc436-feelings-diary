package com.example.feelings_diary

import android.annotation.SuppressLint
import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class ProspectivePatientList (private val context: Activity, private var patients: List<User>) : ArrayAdapter<User>(context,R.layout.prospective_patient_list) {
    @SuppressLint("ViewHolder", "InflateParams")
    override fun getView(position:Int, convertView: View?, parent: ViewGroup): View{
        val inflater = context.layoutInflater
        val listViewItem = inflater.inflate(R.layout.prospective_patient_list,null,true)
        val textViewPatientEmail = listViewItem.findViewById<View>(R.id.textViewPatientEmail) as TextView
        val user = patients[position]
        textViewPatientEmail.text = user.email
        return listViewItem
    }
}