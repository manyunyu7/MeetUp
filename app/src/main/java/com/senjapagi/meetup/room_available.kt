package com.senjapagi.meetup

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.senjapagi.meetup.ModelAdapter.model_room
import com.senjapagi.meetup.ModelAdapter.room_adapter
import com.senjapagi.meetup.Preference.URL
import kotlinx.android.synthetic.main.activity_room_available.*
import org.json.JSONObject

class room_available : AppCompatActivity() {

    lateinit var start: String
    lateinit var end: String
    lateinit var pax: String
    lateinit var paxOrder: String
    lateinit var dialog: SweetAlertDialog

    var data = ArrayList<model_room>()

    lateinit var adapterRoom: room_adapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room_available)



        start = intent.getStringExtra("start")
        end = intent.getStringExtra("end")
        paxOrder = intent.getStringExtra("pax")

        roomCapacity.text = "for $paxOrder pax"
        startTime.text = start
        endTime.text = end

        recyclerViewRoomAvail.setHasFixedSize(true)
        recyclerViewRoomAvail.layoutManager = LinearLayoutManager(
            this, RecyclerView.VERTICAL, false
        )
        adapterRoom = room_adapter(
            data, context = this, accesser = "datePicked", start = start,
            end = end
        )

        getAvail()
        backButton.setOnClickListener {
            super.onBackPressed()
        }
    }

    private fun getAvail() {
        AndroidNetworking.post(URL.ROOM_AVAIL)
            .addBodyParameter("start", start)
            .addBodyParameter("end", end)
            .addBodyParameter("capacity", paxOrder)
            .setPriority(Priority.HIGH)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject?) {
                    var status = response?.getBoolean("sukses")
                    if (status!!) {
                        val head = response
                            ?.getJSONObject("data")
                            ?.getJSONArray("dataroom")

                        for (i in 0 until head?.length()!!) {
                            val id = head.getJSONObject(i).getString("id")
                            val name = head.getJSONObject(i).getString("name")
                            val pax = head.getJSONObject(i).getString("capacity")
                            val thumbnail = head.getJSONObject(i).getString("thumbnail")
                            val desc = head.getJSONObject(i).getString("description")
                            val rate = head.getJSONObject(i).getString("rate_avg")


                            data.add(
                                model_room(
                                    id = id,
                                    name = name,
                                    capacity = pax,
                                    thumbnail = thumbnail,
                                    description = desc,
                                    bookCount = "", // empty because there is no data provided in API
                                    rating = rate,
                                    orderPax = paxOrder
                                )
                            )
                        }

                        recyclerViewRoomAvail.apply {
                            adapter = adapterRoom
                            visibility = View.VISIBLE
                        }
                        lottieListLoading.visibility = View.GONE
                        makeToast("Total Ruangan Tersedia ${data.size.toString()}")
                    } else {
                        val pDialog =
                            SweetAlertDialog(this@room_available, SweetAlertDialog.ERROR_TYPE)
                        pDialog.titleText = "Invalid Date"
                        pDialog.contentText = "Please change meeting time to a valid date"
                        pDialog.confirmText = "OK"
                        pDialog.setConfirmClickListener {
                            pDialog.dismissWithAnimation()
                            onBackPressed()
                            finish()
                        }
                        pDialog.show()
                    }
                }

                override fun onError(anError: ANError?) {
                    makeToast(anError?.errorBody.toString())
                }
            })
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    fun makeToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}

