package com.senjapagi.meetup

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_book_room.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [book_room.newInstance] factory method to
 * create an instance of this fragment.
 */
class book_room : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    val calendar = Calendar.getInstance()
    val calendarTime = Calendar.getInstance()
    var choosenDate: String? = null
    var startDateDB: String? = null
    var endDateDB: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        statusBarController(1)
        val myFormat = "dd-MM-yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        startTime.text = sdf.format(calendar.time).toString()
        choosenDate=startTime.text.toString()
        customToggleBookRoom.setOnClickListener { v ->
            (activity as dashboard_client).openCloseNavigationDrawer(v)
        }

        findRoom.setOnClickListener {
            clearError()
            verifyInput()
        }

        btnEndTime.setOnClickListener {
            clearError()
            TimePickerDialog(
                context!!,
                EndTimeListener,
                calendarTime.get(Calendar.HOUR_OF_DAY),
                calendarTime.get(Calendar.MINUTE),
                true
            ).show()
            calendarTime.set(Calendar.SECOND, 0)
        }

        btnStartTime.setOnClickListener {
            clearError()
            TimePickerDialog(
                context!!,
                StartTimeListener,
                calendarTime.get(Calendar.HOUR_OF_DAY),
                calendarTime.get(Calendar.MINUTE),
                true
            ).show()
            calendarTime.set(Calendar.SECOND, 0)
        }

        btnStartDate.setOnClickListener {
            clearError()
            DatePickerDialog(
                context!!,
                dateSetListener,
                // set DatePickerDialog to point to today's date when it loads up
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    val EndTimeListener =
        TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
            calendarTime.set(Calendar.HOUR_OF_DAY, hourOfDay)
            calendarTime.set(Calendar.MINUTE, minute)
            val formatTime = "HH:mm:ss"
            val sdf = SimpleDateFormat(formatTime, Locale.ENGLISH)
            endHour.text = sdf.format(calendarTime.time).toString()
        }

    val StartTimeListener =
        TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
            calendarTime.set(Calendar.HOUR_OF_DAY, hourOfDay)
            calendarTime.set(Calendar.MINUTE, minute)
            val formatTime = "HH:mm:ss"
            val sdf = SimpleDateFormat(formatTime, Locale.ENGLISH)
            startHour.text = sdf.format(calendarTime.time).toString()
        }

    val dateSetListener =
        DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, monthOfYear)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            val myFormat = "dd-MM-yyyy" // format for application
            val databaseFormat = "yyyy-MM-dd" // format for database and API
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            val sdfDatabase = SimpleDateFormat(databaseFormat, Locale.US)
            startTime.text = sdf.format(calendar.time).toString()
            choosenDate = sdfDatabase.format(calendar.time).toString()
        }

    fun verifyInput() {
        if(totalGuest.text.toString().toInt()<1){
            makeToast("Total Guess should be greater than 1",1)
        }else {
            if (choosenDate.isNullOrBlank()) {
                makeToast("Please Choose a Date", 1)
                startTime.error = "Please Choose a Date"
            } else {
                val start = "${choosenDate} ${startHour.text}"
                val end = "${choosenDate} ${endHour.text}"

                val sdf = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
                val startDate = sdf.parse(end)
                val endDate = sdf.parse(start)
                if (endDate.after(startDate)) {
                    makeToast("End hour must be smaller than start hour", 1)
                    endHour.error = "Fix this input"
                    startHour.error = "Fix this input"
                }else{
                    changeLayout(start,end,totalGuest.text.toString())
                }
            }
        }
    }

    private fun changeLayout(start:String,end:String,pax:String){
        val a = Intent(activity,room_available::class.java)
            .apply {
                putExtra("start",start)
                putExtra("end",end)
                putExtra("pax",pax)
            }
            startActivity(a)
    }

    private fun clearError(){
        endHour.error = null
        startHour.error = null
        startTime.error = null
    }

    private fun makeToast(message: String, duration: Int) {
        when (duration) {
            2 -> {
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            }
            1 -> {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_book_room, container, false)
    }

    fun hideStatusBar() {
        if (Build.VERSION.SDK_INT < 16) {
            activity?.window?.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        } else {
            val decorView: View? = activity?.window?.decorView
            val uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN
            decorView?.systemUiVisibility = uiOptions
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment book_room.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            book_room().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}