package com.senjapagi.meetup

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.Spanned
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
import com.senjapagi.meetup.ModelAdapter.model_room_image
import com.senjapagi.meetup.ModelAdapter.room_image_adapter
import com.senjapagi.meetup.Preference.URL
import com.senjapagi.shsd.Preference.Preference
import com.senjapagi.shsd.Preference.prefConstant
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.details_room.*
import kotlinx.android.synthetic.main.list_foto_loading.*
import org.json.JSONObject


class room_details : AppCompatActivity() {
    lateinit var adapterRoomImage: room_image_adapter
    lateinit var data: ArrayList<model_room_image>
    lateinit var recyclerViewRoomPhoto: RecyclerView
    lateinit var getAccesser: String
    lateinit var dialogHelper: DialogHelper

    lateinit var pax: String
    lateinit var start: String
    lateinit var end: String
    lateinit var room_id : String
    lateinit var user_id : String
    lateinit var pref : Preference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.details_room)
        data = ArrayList<model_room_image>()
        getAccesser = intent?.getStringExtra("accesser").toString()
        adapterRoomImage = room_image_adapter(data, this)
        if (getAccesser != "datePicked") {
        btnBookRoom.text = "BACK"
        }else{
            btnBookRoom.text = "BOOK THIS ROOM"
        }
        dialogHelper = DialogHelper(this)
        pref = Preference(this)
        pax = intent?.getStringExtra("pax").toString()
        start = intent?.getStringExtra("start").toString()
        end = intent?.getStringExtra("end").toString()
        room_id = pref.getRoomID().toString()
        user_id = pref.getPrefString(prefConstant.USER_ID).toString()
        val name = intent.getStringExtra("name")
        val idRoom = intent.getStringExtra("id")
        val thumbnails = intent.getStringExtra("thumbnails")
        val capacity = intent.getStringExtra("pax")
        val desc = intent.getStringExtra("desc")
        pref = Preference(this)
        room_id = pref.getRoomID().toString()
        user_id = pref.getPrefString(prefConstant.USER_ID).toString()

        btnBookRoom.setOnClickListener {
            if (getAccesser == "datePicked") {
                dialogHelper
                    .confirmation(
                    "Book $name Room For Your Meeting ?",
                    user_id,start,end,pax,room_id)
            }else{
                onBackPressed()
            }
        }

        rvRoomPhotosDetails.setHasFixedSize(true)
        rvRoomPhotosDetails.layoutManager = LinearLayoutManager(
            this, RecyclerView.HORIZONTAL,
            false
        )
        roomName.text = name
        roomCapacity.text = capacity
        roomDesc.text = fromHtml(desc);
        loadImage(idRoom)
        Picasso.get()
            .load(URL.ROOM_THUMBNAIL + thumbnails)
            .memoryPolicy(MemoryPolicy.NO_CACHE)
            .networkPolicy(NetworkPolicy.NO_CACHE)
            .into(roomThumbnail)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        pref.clearTempRoom()
    }

    private fun fromHtml(html: String?): Spanned? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY)
        } else {
            Html.fromHtml(html)
        }
    }

    fun loadImage(id: String) {
        data.clear()
        AndroidNetworking.get(URL.ROOM_DETAIL)
            .addPathParameter("room_id", id)
            .setPriority(Priority.HIGH)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject?) {
                    val baseData = response?.getJSONObject("data")
                        ?.getJSONArray("dataroomphoto")

//                    makeToast("Jumlah Foto  ${baseData?.length().toString()}")
                    for (i in 0 until baseData?.length()!!) {
                        val photo = baseData
                            ?.getJSONObject(i)
                            ?.getString("photo")
                        data.add(model_room_image(id, photo.toString()))
                        rvRoomPhotosDetails.adapter = adapterRoomImage
                    }
                    rvRoomPhotosDetails.visibility = View.VISIBLE
                    placeHolderLoadingImage.visibility = View.GONE
                }

                override fun onError(anError: ANError?) {
//                    TODO("Not yet implemented")
                    makeToast(anError?.errorDetail.toString())
                    makeToast(anError?.errorBody.toString())
                }
            })
    }

    fun makeToast(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }





}


class DialogHelper constructor(mContext: Context) : AppCompatActivity() {
    lateinit var pDialog: SweetAlertDialog
    var mContext = mContext


    fun success(message: String){
        pDialog = SweetAlertDialog(mContext, SweetAlertDialog.SUCCESS_TYPE)
        pDialog.apply {
            titleText = "Booking Successfully Inputed"
            contentText = message
            confirmText = "Yes"
            setConfirmClickListener { sDialog ->
                //TODO : ADD INTENT TO SUCCESS
                sDialog.dismissWithAnimation()
            }
        }
        pDialog.show()
    }

    fun changeLayout(){

        val a = Intent(mContext,dashboard_client::class.java)
        a.putExtra("success_booking","1")
        mContext.startActivity(a)
        finish()
    }

    fun confirmation(message:String,user:String,start:String,end:String,pax:String,room:String) {
        pDialog = SweetAlertDialog(mContext, SweetAlertDialog.SUCCESS_TYPE)
        pDialog.apply {
            titleText = "Are you sure"
            contentText = message
            confirmText = "Yes"
            setConfirmClickListener { sDialog ->
                //TODO : CALL INPUT METHOD
                inputOrder(user,start,end,pax,room)
                sDialog.dismissWithAnimation()

            }
            showCancelButton(true)
            cancelText = "Cancel"
            setCancelClickListener { sweetAlertDialog ->
                sweetAlertDialog.dismissWithAnimation()
            }
        }
        pDialog.show()

    }

    fun destroy() {
        pDialog.dismissWithAnimation()
    }

    fun inputOrder(user_id:String,start:String,end:String,pax:String,room_id:String){
        AndroidNetworking.post(URL.PLACE_BOOKING)
            .addHeaders("user_id", user_id)
            .addBodyParameter("start", start)
            .addBodyParameter("end", end)
            .addBodyParameter("pax", pax)
            .addBodyParameter("room_id", room_id)
            .setPriority(Priority.HIGH)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject?) {
                    if(response?.getBoolean("sukses")!!){
                        success("Check your booking status at Active Booking Menu")
                        changeLayout()
                    }else{
                        makeToast("""
                            user id : $user_id
                            start : $start
                            end : $end
                            pax : $pax
                            room : $room_id
                        """.trimIndent())
                        makeToast(response.toString())
                    }
                }

                override fun onError(anError: ANError?) {
                    makeToast(anError?.errorDetail.toString())
                    makeToast(anError?.errorBody.toString())
                }
            })
    }
    fun makeToast(message: String) {
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show()
    }
}