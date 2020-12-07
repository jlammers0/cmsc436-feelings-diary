package com.example.feelings_diary

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.CalendarContract
import android.text.format.DateFormat
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
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
    private var databaseTherapists: DatabaseReference? = null
    private var curUser: User? = null

    private var therapistAdapter: ArrayAdapter<User>? = null




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



        therapistList = ArrayList()
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
            //TODO: create reminders to check in and option to delete account
            //patient settings will no longer add therapist we have add therapist button for this
            //patient settings will need to be able to show and remove connected therapist

            // Build and inflate settings fragment
            val dialogBuilder = AlertDialog.Builder(this)
            val inflater = layoutInflater
            val dialogView = inflater.inflate(R.layout.patient_settings,null)
            dialogBuilder.setView(dialogView)

            // Get Fragment Buttons
            val deleteButton = dialogView.findViewById<Button>(R.id.deleteButton)
            val removeTherapistButton = dialogView.findViewById<Button>(R.id.removeTherapistButton)

            // Onclick for delete user button
            deleteButton.setOnClickListener {
                // Get current user
                val currentUser = mAuth!!.currentUser

                try {
                    // Delete account
                    currentUser!!.delete()

                    // Notify user that account has been deleted
                    Toast.makeText(this, "You have successfully deleted your account.", Toast.LENGTH_LONG)

                    // Go back to main activity
                    startActivity(Intent(this, MainActivity::class.java))
                } catch (e: Exception) {
                    // Notify user that account couldn't be deleted
                    Toast.makeText(this, "Something went wrong. Please try again later.", Toast.LENGTH_LONG)
                }
            }

            // Onclick for remove therapist button
            removeTherapistButton.setOnClickListener {
                // TODO Figure out a way to remove a therapist from this patient's list
                // Maybe have a therapist ListView in this fragment??
            }
        }

        calendarButton!!.setOnClickListener{
            // Get reference to fragment manager
            val manager = supportFragmentManager

            // Show Date picker
            DatePickerFragment(this, manager).show(manager, "Patient Date Picker")
        }


        logoutButton!!.setOnClickListener{
            mAuth!!.signOut()
            Toast.makeText(applicationContext,"You have been successfully logged out", Toast.LENGTH_LONG).show()
            startActivity(Intent(this@PatientHomeActivity,MainActivity::class.java).putExtra(USER_ID,
                mAuth!!.currentUser!!.uid))
        }

        patientAddTherapistButton!!.setOnClickListener{

            val dialogBuilder = AlertDialog.Builder(this)
            val inflater = layoutInflater
            val dialogView = inflater.inflate(R.layout.patient_find_therapist,null)
            dialogBuilder.setView(dialogView)

            val enrolledTherapistListView = dialogView.findViewById<View>(R.id.enrolledTherapistList) as ListView
            val findTherapistByEmail = dialogView.findViewById<View>(R.id.findTherapistByEmail) as EditText
            val requestTherapistButton = dialogView.findViewById<View>(R.id.requestTherapistButton) as Button



            therapistAdapter = ProspectiveTherapistList(this,
                therapistList as ArrayList<User>
            )
            enrolledTherapistListView.adapter = therapistAdapter

            dialogBuilder.setTitle("Find therapist")
            val b = dialogBuilder.create()

            b.show()
            for(therapist in therapistList as ArrayList<User>) {
                Log.i(TAG, "therapistList contains ${therapist.email}")
            }

            //not sure why this cast is needed. might have something to do with being set to null at first
            //and not declared as lateinit
            enrolledTherapistListView.onItemClickListener = AdapterView.OnItemClickListener{_,_,i,_ ->
                findTherapistByEmail.setText(therapistList!![i].email)

            }




            requestTherapistButton.setOnClickListener {




                if (findTherapistByEmail.text.toString().isNullOrEmpty()){
                    Toast.makeText(applicationContext,"Select a therapist from the list or enter therapist email manually",Toast.LENGTH_LONG).show()
                }
                var existsFlag = false;
                for (therapist in therapistList as ArrayList<User>){
                    if (therapist.email == findTherapistByEmail.text.toString()){
                        existsFlag =true
                        FirebaseDatabase.getInstance().reference.child("prospectivePatients").child(therapist.uid).child(uid!!).setValue(curUser)
                    }
                }
                if (existsFlag){
                    Toast.makeText(applicationContext,"Therapist has been sent a patient request",Toast.LENGTH_LONG).show()
                }else{
                    Toast.makeText(applicationContext,"Therapist was not found",Toast.LENGTH_LONG).show()
                }

                //TODO request therapist

                b.dismiss()

            }

            //TODO: build therapist list and add node to firebase connecting patients and therapists
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

                for (data in snapshot.children){
                    val temp = data.getValue(User::class.java)
                    if (temp!!.email == uemail!!){
                        curUser=temp

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
    }

    companion object{
        const val TAG = "feelings-diary-log"
        const val USER_ID = "com.example.tesla.myhomelibrary.userid"
        const val USER_EMAIL = "com.example.tesla.myhomelibrary.useremail"
        const val DATE_SELECTED = "date selected"
    }
}

/* Date Picker for calendar support*/
private class DatePickerFragment(context: Context, manager: FragmentManager) : DialogFragment(), DatePickerDialog.OnDateSetListener {

    private val mContext = context
    private val mManager = manager

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
        val tPicker = TimePickerFragment()

        // Show Time Picker
        tPicker.show(mManager, "Patient Time Picker")

        // Get picked time in milliseconds
        val time = Calendar.getInstance().run {
            set(year, month, day, tPicker.mHour, tPicker.mMinute)
            timeInMillis
        }

        // Start calendar activity
        val calIntent = Intent(Intent.ACTION_INSERT)
            .setData(CalendarContract.Events.CONTENT_URI)
            .putExtra(CalendarContract.Events.TITLE, "Patient Check-In")
            .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, time)

        startActivity(calIntent)
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