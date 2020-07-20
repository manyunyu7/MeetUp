package com.senjapagi.meetup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.senjapagi.meetup.ModelAdapter.model_room
import com.senjapagi.meetup.ModelAdapter.room_adapter
import com.senjapagi.meetup.Preference.URL
import kotlinx.android.synthetic.main.fragment_room_catalog.*
import kotlinx.android.synthetic.main.fragment_room_catalog.lottieListLoading
import kotlinx.android.synthetic.main.fragment_user_book_history.*
import kotlinx.android.synthetic.main.list_foto_loading.*
import org.json.JSONArray
import org.json.JSONObject

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [room_catalog.newInstance] factory method to
 * create an instance of this fragment.
 */
class room_catalog : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var adapterRoom : room_adapter
    var data = ArrayList<model_room>()

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
        recyclerViewRoom.setHasFixedSize(true)
        recyclerViewRoom.layoutManager = LinearLayoutManager(
            context,RecyclerView.VERTICAL,false)
        retreiveRoom()
        adapterRoom= room_adapter(data,context!!,accesser = "notPicked",start = "",end = "")

        super.onViewCreated(view, savedInstanceState)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_room_catalog, container, false)
    }

    fun retreiveRoom(){
        data.clear()
        lottieListLoading.visibility = View.VISIBLE
        AndroidNetworking.get(URL.ROOM_ALL)
            .setPriority(Priority.HIGH)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener{
                override fun onResponse(response: JSONObject?) {
                    lottieListLoading.visibility = View.GONE
                    //TODO("Not yet implemented")
                    val status = response?.getBoolean("sukses")
                    if(status!!){
                        val totalRoom = response.getJSONArray("data")
                            .length()
                        val raz = response.getJSONArray("data")
                        for (i in 0 until raz.length()){
                            val id = raz.getJSONObject(i).getString("id")
                            val name = raz.getJSONObject(i).getString("name")
                            val capacity = raz.getJSONObject(i).getString("capacity")
                            val thumbnail = raz.getJSONObject(i).getString("thumbnail")
                            val desc = raz.getJSONObject(i).getString("description")
                            val counter = raz.getJSONObject(i).getString("booked_count")
                            val rate = raz.getJSONObject(i).getString("rate_avg")
                            data.add(model_room(
                                id,name,capacity,thumbnail,desc,"Booked $counter times",rate,"0"
                            ))
                            recyclerViewRoom.adapter = adapterRoom
                            recyclerViewRoom.visibility = View.VISIBLE
                            lottieListLoading.visibility = View.GONE
                        }


                    }else{
                        makeToast("status gagal")
                    }


                }

                override fun onError(anError: ANError) {
                    //TODO("Not yet implemented")
                    lottieListLoading.visibility = View.GONE
                    makeToast("onError")
                    makeToast("Error "+anError.errorBody.toString())
                }

            })
    }



    fun makeToast(message : String){
        Toast.makeText(context,message,Toast.LENGTH_LONG).show()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment room_catalog.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            room_catalog().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}