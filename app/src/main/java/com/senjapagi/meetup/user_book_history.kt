package com.senjapagi.meetup

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.senjapagi.meetup.ModelAdapter.model_order_basic
import com.senjapagi.meetup.ModelAdapter.order_basic_adapter
import com.senjapagi.meetup.ModelAdapter.room_adapter
import com.senjapagi.meetup.Preference.URL
import com.senjapagi.shsd.Preference.Preference
import com.senjapagi.shsd.Preference.prefConstant
import kotlinx.android.synthetic.main.fragment_active_booking.*
import kotlinx.android.synthetic.main.fragment_room_catalog.*
import kotlinx.android.synthetic.main.fragment_room_catalog.loading_indicator
import kotlinx.android.synthetic.main.fragment_room_catalog.lottieListLoading
import org.json.JSONObject

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [user_book_history.newInstance] factory method to
 * create an instance of this fragment.
 */
class user_book_history : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var adapterOrder: order_basic_adapter
    var data = ArrayList<model_order_basic>()

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
        recyclerViewOrder.setHasFixedSize(true)
        recyclerViewOrder.layoutManager = LinearLayoutManager(
            context, RecyclerView.VERTICAL,false)
        retreiveOrder()
        adapterOrder= order_basic_adapter(data,context!!,getView()!!)
        super.onViewCreated(view, savedInstanceState)
    }


    fun retreiveOrder() {
        data.clear()
        val pref = Preference(activity?.applicationContext!!)
        loading_indicator.visibility = View.VISIBLE
        AndroidNetworking.get(URL.ORDER_LIST_BY_USER)
            .addHeaders("user_id",pref.getPrefString(prefConstant.USER_ID))
            .setPriority(Priority.HIGH)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject?) {
                    loading_indicator.visibility = View.GONE
                    //TODO("Not yet implemented")
                    val status = response?.getBoolean("sukses")
                    if (status!!) {

                        val totalRoom = response.getJSONArray("data")
                            .length()

                        if(totalRoom<1){
                            recyclerViewOrder.visibility = View.VISIBLE
                            lottieListLoading.visibility = View.GONE
                            makeToast("You have not book any room yet")
                        }
                        makeToast(totalRoom.toString())
                        val raz = response.getJSONArray("data")
                        for (i in 0 until raz.length()) {
                            val id = raz.getJSONObject(i).getString("id")
                            val room_name = raz.getJSONObject(i).getString("room_name")
                            val pax = raz.getJSONObject(i).getString("pax")
                            var status = raz.getJSONObject(i).getString("status")
                            var start = raz.getJSONObject(i).getString("start")
                            var end = raz.getJSONObject(i).getString("end")
                            val thumbnails = raz.getJSONObject(i).getString("room_thumbnail")
                            val rate = raz.getJSONObject(i).getString("rate")
                            val comment = raz.getJSONObject(i).getString("comment")

                            val sdf = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                            val startDatex = java.util.Date(start.toString().toLong() * 1000)
                            val endDatex = java.util.Date(end.toString().toLong() * 1000)

                            start = sdf.format(startDatex).toString()
                            end = sdf.format(endDatex).toString()
                            
                            data.add(
                                model_order_basic(
                                    id = "$id",
                                    room_name = "$room_name",
                                    pax = "$pax",
                                    status = "$status",
                                    start = "$start",
                                    end = "$end",
                                    thumbnails = "$thumbnails",
                                    comment = comment,
                                    rate = rate
                                )
                            )

                            if (data.size <1){
                                recyclerViewOrder.visibility = View.VISIBLE
                                lottieListLoading.visibility = View.GONE
                            }

                            adapterOrder = order_basic_adapter(data,context,view!!)
                            recyclerViewOrder.adapter = adapterOrder
                            recyclerViewOrder.visibility = View.VISIBLE
                            lottieListLoading.visibility = View.GONE
                        }

                    } else {
                            recyclerViewOrder.visibility = View.VISIBLE
                            lottieListLoading.visibility = View.GONE
                    }
                }

                override fun onError(anError: ANError) {
                    //TODO("Not yet implemented")
                    loading_indicator.visibility = View.GONE
                    makeToast("onError")
                    makeToast("Error " + anError.errorBody.toString())
                }

            })
    }

    fun makeToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_book_history, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment user_book_history.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            user_book_history().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}