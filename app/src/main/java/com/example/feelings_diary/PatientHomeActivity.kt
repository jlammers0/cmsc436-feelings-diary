package com.example.feelings_diary

import android.app.*
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.CalendarContract
import android.text.format.DateFormat
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.*
import kotlin.collections.ArrayList

class PatientHomeActivity : AppCompatActivity() {

    private var calendarView:CalendarView?=null
    private var checkInButton:Button?=null
    private var listButton:Button?=null
    private var mailButton:ImageButton?=null
    private var calendarButton:ImageButton?=null
    private var settingsButton:ImageButton?=null
    private var patientAddTherapistButton: ImageButton?= null
    private var logoutButton:ImageButton?=null
    private var mAuth: FirebaseAuth? = null
    private var mDatabase: FirebaseDatabase? = null
    private var uid:String? = null
    private var uemail:String? = null
    private var therapistList:MutableList<User>? = null
    private var patientNotificationList: MutableList<PatientNotification>? = null
    private var databaseTherapists: DatabaseReference? = null
    private var users:MutableList<User>? = null
    private var databasePatients: DatabaseReference? = null
    private var personalTherapist: String? = null
    private var curUser: User? = null

    private var therapistAdapter: ArrayAdapter<User>? = null
    private var notificationAdapter : ArrayAdapter<PatientNotification>? = null




    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.patient_home)
        Log.i(TAG,"Entered PatientHomeActivity")

        calendarView = findViewById(R.id.calendarView)
        checkInButton = findViewById(R.id.patientCheckInButton)
        listButton = findViewById(R.id.patientViewCheckInButton)
        mailButton = findViewById(R.id.patientMailButton)
        calendarButton = findViewById(R.id.patientCalendarButton)
        settingsButton = findViewById(R.id.patientSettingsButton)
        logoutButton = findViewById(R.id.patientLogoutButton)
        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance()
        patientAddTherapistButton = findViewById(R.id.patientAddTherapistButton)
        uid = intent.getStringExtra(USER_ID)
        uemail = intent.getStringExtra(USER_EMAIL)

        databasePatients = FirebaseDatabase.getInstance().getReference("patients")



        users = ArrayList()
        therapistList = ArrayList()
        patientNotificationList = ArrayList()
        databaseTherapists = FirebaseDatabase.getInstance().getReference("therapists")


        checkInButton!!.setOnClickListener {
            val dialogBuilder = AlertDialog.Builder(this)
            val inflater = layoutInflater
            val dialogView = inflater.inflate(R.layout.patient_check_in,null)
            dialogBuilder.setView(dialogView)
            //TODO: patient_check_in is a template. Check in still needs more work
            //needs extra faces, attach slider to faces with corresponding ints
            //firebase node "diary" to be added to store patient check ins
            startActivity(Intent(this@PatientHomeActivity,CheckInActivity::class.java).putExtra(USER_ID, uid).putExtra(
                USER_EMAIL,uemail))
        }


        //TODO: calendarView
        //get selected date and create a list of check-ins on that date
        //this view has not been created yet. dialog or activity with a listview similar
        //to patientList or messages will do
        listButton!!.setOnClickListener{
            val selectedDate = calendarView!!.date.toString()
            startActivity(Intent(this@PatientHomeActivity, CheckInListActivity::class.java).putExtra(USER_ID, uid).putExtra(
                DATE_SELECTED, selectedDate).putExtra(USER_EMAIL, uemail)
            )

        }

        settingsButton!!.setOnClickListener{
            Log.i(TAG,"SettingsButton clicked")
            //TODO: create reminders to check in and option to delete account
            //patient settings will no longer add therapist we have add therapist button for this
            //patient settings will need to be able to show and remove connected therapist

            // Build and inflate settings fragment
            val dialogBuilder = AlertDialog.Builder(this)
            val inflater = layoutInflater
            val dialogView = inflater.inflate(R.layout.patient_settings,null)
            dialogBuilder.setView(dialogView)

            // Get Fragment Buttons
            val addNotificationButton = dialogView.findViewById<View>(R.id.addNotificationButton) as Button
            val removeTherapistButton = dialogView.findViewById<View>(R.id.removeTherapistButton) as Button
            val removeNotificationsButton = dialogView.findViewById<View>(R.id.removeNotificationButton) as Button
            val theruname = dialogView.findViewById<View>(R.id.theruname) as TextView
            val theremail = dialogView.findViewById<View>(R.id.theremail) as TextView
            val patientNotificationListView = dialogView.findViewById<ListView>(R.id.patientNotificationsList)

            dialogBuilder.setTitle("Patient Settings")
            val b = dialogBuilder.create()

            b.show()
            Log.i(TAG,"DialogBuilder should be showing")


            // Initialize notification list's adapter
            notificationAdapter = PatientNotificationList(this,
                (patientNotificationList as List<PatientNotification>))
            patientNotificationListView.adapter = notificationAdapter


            if (!personalTherapist.isNullOrEmpty()) {
                for (x in users!!) {
                    if (x.uid == personalTherapist!!) {
                        theruname.text = x.uname
                        theremail.text = x.email

                    }
                }
            }

            // OnClick for add notification button
            addNotificationButton.setOnClickListener {
                // Get refs to time and date pickers
                val datePicker = DatePickerFragment(this)
                val timePicker = TimePickerFragment()

                // Show date and time pickers
                datePicker.show(supportFragmentManager, "Date selection")
                timePicker.show(supportFragmentManager, "Time Selection")

                // Convert user selection into PatientNotification()
                val newNotification = PatientNotification(
                    datePicker.mYear,
                    datePicker.mMonth,
                    datePicker.mDay,
                    timePicker.mHour,
                    timePicker.mMinute
                )

                // Add new notification to list and notify adapter
                (patientNotificationList as ArrayList).add(newNotification)
                (notificationAdapter as PatientNotificationList).notifyDataSetChanged()

                // Create pending intent for notification
                val notIntent = Intent(this, LoginActivity::class.java)
                val pendingIntent = PendingIntent.getActivity(this, 0, notIntent, 0)

                // Get reference to notification builder
                var builder = NotificationCompat.Builder(this, CHAN_ID)
                    .setContentTitle("Check-In Reminder")
                    .setContentText("This is a reminder that you have a check-in!")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)

                // TODO add support for scheduled notifications
            }

            // OnClick for remove notification button
            removeNotificationsButton.setOnClickListener {
                val currentNot = patientNotificationListView.selectedItem as PatientNotification

                (patientNotificationList as ArrayList<PatientNotification>).remove(currentNot)

                val adapter = patientNotificationListView.adapter
                (adapter as ArrayAdapter<PatientNotification>).notifyDataSetChanged()

                // TODO remove scheduled notification
            }

            // Onclick for remove therapist button
            removeTherapistButton.setOnClickListener {
                if (personalTherapist.isNullOrEmpty()){
                    Toast.makeText(applicationContext,"You do not have a connected therapist to remove",Toast.LENGTH_SHORT).show()
                }else{
                    databasePatients!!.child(personalTherapist!!).child(uid!!).removeValue()
                }

                b.dismiss()
            }


        }

        calendarButton!!.setOnClickListener{




            val calendarEventTime = System.currentTimeMillis()

            val builder = CalendarContract.CONTENT_URI.buildUpon().appendPath("time")
            ContentUris.appendId(builder,calendarEventTime)
            val uri = builder.build()
            val intent = Intent(Intent.ACTION_VIEW).setData(uri)
            startActivity(intent)

        }


        logoutButton!!.setOnClickListener{
            mAuth!!.signOut()
            Toast.makeText(applicationContext,"You have been successfully logged out", Toast.LENGTH_LONG).show()
            startActivity(Intent(this@PatientHomeActivity,MainActivity::class.java))
        }

        patientAddTherapistButton!!.setOnClickListener{

            if(personalTherapist.isNullOrEmpty()) {

                val dialogBuilder = AlertDialog.Builder(this)
                val inflater = layoutInflater
                val dialogView = inflater.inflate(R.layout.patient_find_therapist, null)
                dialogBuilder.setView(dialogView)

                val enrolledTherapistListView =
                    dialogView.findViewById<View>(R.id.enrolledTherapistList) as ListView
                val findTherapistByEmail =
                    dialogView.findViewById<View>(R.id.findTherapistByEmail) as EditText
                val requestTherapistButton =
                    dialogView.findViewById<View>(R.id.requestTherapistButton) as Button



                therapistAdapter = ProspectiveTherapistList(
                    this,
                    therapistList as ArrayList<User>
                )
                enrolledTherapistListView.adapter = therapistAdapter

                dialogBuilder.setTitle("Find therapist")
                val b = dialogBuilder.create()

                b.show()
                for (therapist in therapistList as ArrayList<User>) {
                    Log.i(TAG, "therapistList contains ${therapist.email}")
                }

                //not sure why this cast is needed. might have something to do with being set to null at first
                //and not declared as lateinit
                enrolledTherapistListView.onItemClickListener =
                    AdapterView.OnItemClickListener { _, _, i, _ ->
                        findTherapistByEmail.setText(therapistList!![i].email)

                    }




                requestTherapistButton.setOnClickListener {

                    for (user in users!!) {
                        if (user.email == uemail) {
                            curUser = user
                        }
                    }




                    if (findTherapistByEmail.text.toString().isNullOrEmpty()) {
                        Toast.makeText(
                            applicationContext,
                            "Select a therapist from the list or enter therapist email manually",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    var existsFlag = false;
                    for (therapist in therapistList as ArrayList<User>) {
                        if (therapist.email == findTherapistByEmail.text.toString()) {
                            existsFlag = true
                            FirebaseDatabase.getInstance().reference.child("prospectivePatients")
                                .child(therapist.uid).child(uid!!).setValue(curUser)
                        }
                    }
                    if (existsFlag) {
                        Toast.makeText(
                            applicationContext,
                            "Therapist has been sent a patient request",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        Toast.makeText(
                            applicationContext,
                            "Therapist was not found",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    //TODO request therapist

                    b.dismiss()

                }
            }else{
                Toast.makeText(applicationContext,"You cannot add more than one therapist",Toast.LENGTH_SHORT).show()
            }


        }

        mailButton!!.setOnClickListener{
            startActivity(Intent(this@PatientHomeActivity,MailInboxActivity::class.java).putExtra(USER_ID,uid).putExtra(
                USER_EMAIL,uemail))

        }

    }

    override fun onStart() {
        super.onStart()

        FirebaseDatabase.getInstance().getReference("users").addListenerForSingleValueEvent(object:
            ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                var temp:User? = null
                for (data in snapshot.children){

                    try {
                        temp = data.getValue(User::class.java)
                    }catch (e:Exception){
                        Log.e(TAG,e.toString())
                    }finally{
                        users!!.add(temp!!)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.i(TAG,"fetching users failed")
            }

        })
        databaseTherapists!!.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                therapistList!!.clear()
                var user: User? = null
                for (data in snapshot.children){
                    try {
                        user = data.getValue(User::class.java)
                    }catch (e:Exception){
                        Log.e(TAG,e.toString())
                    }finally{
                        therapistList!!.add(user!!)
                    }
                }



            }




            override fun onCancelled(error: DatabaseError) {
                Log.i(TherapistHomeActivity.TAG, "loading patients was canceled")
            }
        })
        databasePatients!!.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                personalTherapist = ""
                var user: User? = null
                for (data in snapshot.children){
                    for (x in data.children){
                        try {
                            user = x.getValue(User::class.java)
                        }catch (e:Exception){
                            Log.e(TAG,e.toString())
                        }finally{
                            if (user!!.uid == uid){
                                personalTherapist = data.key!!
                            }
                        }
                    }
                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    companion object{
        const val TAG = "feelings-diary-log"
        const val USER_ID = "com.example.tesla.myhomelibrary.userid"
        const val USER_EMAIL = "com.example.tesla.myhomelibrary.useremail"

        const val DATE_SELECTED = "date selected"

        const val CHAN_ID = "feelings-diary-id"

    }
}

/* Date Picker for calendar support*/
private class DatePickerFragment(context: Context) : DialogFragment(), DatePickerDialog.OnDateSetListener {

    private val mContext = context

    var mYear: Int = 0
    var mMonth: Int = 0
    var mDay: Int = 0

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the current date as the default date in the picker
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        // Create a new instance of DatePickerDialog and return it
        return DatePickerDialog(mContext, this, year, month, day)
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
        // Set date
        mYear = year; mMonth = month; mDay = day;
    }
}

/* Time Picker for Calendar Support */
class TimePickerFragment : DialogFragment(), TimePickerDialog.OnTimeSetListener {

    var mHour: Int = 0
    var mMinute: Int = 0

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the current time as the default values for the picker
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)

        // Create a new instance of TimePickerDialog and return it
        return TimePickerDialog(activity, this, hour, minute, DateFormat.is24HourFormat(activity))
    }

    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
        mHour = hourOfDay
        mMinute = minute
    }
}