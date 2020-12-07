package com.example.feelings_diary

import android.annotation.SuppressLint
import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.firebase.database.FirebaseDatabase

class CheckInAdapter (private val context: Activity, private var check: List<DiaryEntry>, private var uid: String?):
    ArrayAdapter<DiaryEntry> (context, R.layout.layout_checkin_list, check){
    @SuppressLint("ViewHolder", "InflateParams", "SetTextI18n")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = context.layoutInflater
        val listViewItem = inflater.inflate(R.layout.layout_checkin_list,null,true)

        val checkInText = listViewItem.findViewById<View>(R.id.comment_text) as TextView
        val dateText = listViewItem.findViewById<View>(R.id.date_text) as TextView
        val scoreText = listViewItem.findViewById<View>(R.id.score_text) as TextView


        val entry = check[position]
        checkInText.text = entry.comment
        dateText.text = entry.date
        scoreText.text = entry.feeling.toString()
        if(entry.therapistComment != ""){
            val titleView = listViewItem.findViewById<TextView>(R.id.therapist_title)
            titleView.text = context.getString(R.string.therapist_comment)
            titleView.visibility = View.VISIBLE

            val textView = listViewItem.findViewById<TextView>(R.id.therapist_comment)
            textView.text = entry.therapistComment
            textView.visibility = View.VISIBLE
        }
        if(context is TCICommentActivity){
            val pNameText = listViewItem.findViewById<TextView>(R.id.comment_Title)
            pNameText.text = "Patient's Comment:"
            val pScoreText = listViewItem.findViewById<TextView>(R.id.score_title)
            pScoreText.text = "Patient's Happiness Score:"

            val editText = listViewItem.findViewById<EditText>(R.id.therapist_input)
            editText.visibility = View.VISIBLE
            val saveButton = listViewItem.findViewById<Button>(R.id.therapist_save_button)
            saveButton.visibility = View.VISIBLE
            saveButton.setOnClickListener{
                entry.therapistComment = editText.text.toString()
                val mDatabaseReference = FirebaseDatabase.getInstance().reference
                mDatabaseReference.child("diary").child(uid!!).child(entry.date).setValue(entry)
                Toast.makeText(context, "Comment saved. The patient will be able to view your comment",
                    Toast.LENGTH_LONG).show()
                saveButton.visibility = View.GONE
                editText.visibility = View.GONE
            }
        }
        return listViewItem

    }
}