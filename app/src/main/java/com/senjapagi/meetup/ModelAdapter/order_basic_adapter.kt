package com.senjapagi.meetup.ModelAdapter

import android.content.Context
import android.os.Build
import android.text.Html
import android.text.Spanned
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.RatingBar.OnRatingBarChangeListener
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.senjapagi.meetup.Preference.URL
import com.senjapagi.meetup.R
import com.senjapagi.meetup.user_book_history
import com.senjapagi.shsd.Preference.Preference
import com.senjapagi.shsd.Preference.prefConstant
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.details_room.*
import kotlinx.android.synthetic.main.fragment_active_booking.view.*
import kotlinx.android.synthetic.main.fragment_user_profile.view.*
import kotlinx.android.synthetic.main.layout_book_details.view.*
import kotlinx.android.synthetic.main.list_order.view.*
import org.json.JSONObject


class order_basic_adapter(
    private val order: ArrayList<model_order_basic>,
    val context: Context?,
    val view: View
) : RecyclerView.Adapter<orderBasicHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): orderBasicHolder {
        //      TODO("Not yet implemented")
        return orderBasicHolder(
            LayoutInflater.from(context).inflate(R.layout.list_order, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return order.size
    }

    override fun onBindViewHolder(holder: orderBasicHolder, position: Int) {
        holder.tvOrderRoomName.text = order.get(position).room_name
        holder.tvOrderPax.text = order.get(position).pax
        holder.tvOrderID.text = order.get(position).id
        holder.tvStart.text = order.get(position).start
        holder.tvEnd.text = order.get(position).end
        holder.tvStatus.text = order[position].status


        var review = order[position].comment
        var roomName = order[position].room_name
        val rating: String? = order[position].rate
        var iRating: Float = 0f
        iRating = rating?.toFloat() ?: 0f

        var barRating = 0

        when (iRating) {
            1f -> {
                holder.tvRoomRatingIndicator.text = "⭐"
                barRating = 1
            }
            in 1f..2f -> {
                holder.tvRoomRatingIndicator.text = "⭐⭐"
                barRating == 2
            }
            2f -> {
                holder.tvRoomRatingIndicator.text = "⭐⭐"
                barRating == 2
            }
            in 2f..3f -> {
                holder.tvRoomRatingIndicator.text = "⭐⭐⭐"
                barRating = 3
            }
            3f -> {
                holder.tvRoomRatingIndicator.text = "⭐⭐⭐"
                barRating = 2
            }
            in 3f..4f -> {
                holder.tvRoomRatingIndicator.text = "⭐⭐⭐⭐"
                barRating = 3
            }
            4f -> {
                holder.tvRoomRatingIndicator.text = "⭐⭐⭐⭐"
                barRating = 4
            }
            in 4f..5f -> {
                holder.tvRoomRatingIndicator.text = "⭐⭐⭐⭐⭐"
                barRating = 4
            }
            5f -> {
                holder.tvRoomRatingIndicator.text = "⭐⭐⭐⭐⭐"
                barRating = 5
            }
            null -> {
                holder.tvRoomRatingIndicator.text = "Not Rated Yet"
                barRating = 0
            }
            else -> {
                holder.tvRoomRatingIndicator.text = "Not Rated Yet"
                barRating = 0
                Toast.makeText(context, iRating.toString(), Toast.LENGTH_SHORT).show()
            }
        }


        if (order[position].status.equals("rejected")) {
            holder.containerStatus.setImageDrawable(
                context?.resources?.getDrawable(R.drawable.bootstrap_red_rounded, context.theme)
            )
        }
        if (order[position].status.equals("waiting")) {
            holder.containerStatus.setImageDrawable(
                context?.resources?.getDrawable(R.drawable.bootstrap_orange_rounded, context.theme)
            )
        }
        if (order[position].status.equals("accepted")) {
            holder.containerStatus.setImageDrawable(
                context?.resources?.getDrawable(R.drawable.bootstrap_blue_rounded, context.theme)
            )


            //setImageDrawable(getResources().getDrawable(R.drawable.img1))
        }

        val thumbnail = order.get(position).thumbnails

        Picasso.get()
            .load(URL.ROOM_THUMBNAIL + thumbnail)
            .memoryPolicy(MemoryPolicy.NO_CACHE)
            .networkPolicy(NetworkPolicy.NO_CACHE)
            .into(holder.ivRoomThumbnail)

        holder.itemView.setOnClickListener {



            val check = order[position].status
            if (check != "accepted") {
                view.ratingContainer.visibility = View.GONE
                view.nonRatingContainer.visibility = View.VISIBLE
            } else if (check == "accepted") {
                view.ratingContainer.visibility = View.VISIBLE
                view.nonRatingContainer.visibility = View.GONE
                view.btnDeleteOrder.visibility =View.GONE
            }
            if(check=="waiting"){
                view.btnDeleteOrder.visibility =View.VISIBLE
            }
            if (check == "rejected") {
                view.btnDeleteOrder.visibility = View.GONE
            }

            var review = order[position].comment
            var e = view.etReview
            if (review.isNullOrBlank() || review.equals("null")) {
                e.hint = "write your experience here"
            } else {
                e.setText(fromHtml(review))
            }


            view.lyt_book_details.visibility = View.VISIBLE
            view.lyt_book_details.animation =
                AnimationUtils.loadAnimation(context, R.anim.item_animation_falldown)

            view.ratingBar.setOnClickListener {
                val msg = view.ratingBar.rating.toString()
                view.ratingStatus.text = msg
            }

            view.btnSaveRating.setOnClickListener {
                val pref = Preference(context!!)
                val orderID = order[position].id
                AndroidNetworking.post(
                    //api/user/daftarpemesanan/
                    //{order_id}/rate/edit
                    "http://103.253.27.125:10000/booking/index.php?/api/user/daftarpemesanan/$orderID/rate/edit")
                    .addBodyParameter("rate", view.ratingBar.rating.toString())
                    .addBodyParameter("comment", view.etReview.text.toString())
                    .build()
                    .getAsJSONObject(object : JSONObjectRequestListener {
                        override fun onResponse(response: JSONObject?) {
                            makeToast("Thanks for your review !!")
                            view.lyt_book_details.animation =
                                AnimationUtils.loadAnimation(context, R.anim.item_animation_fallup)
                            view.lyt_book_details.visibility = View.GONE
                            view.recyclerViewOrder.adapter = this@order_basic_adapter
                            order[position].rate= view.ratingBar.rating.toFloat().toString()
                            order[position].comment=view.etReview.text.toString()
                            view.ratingBar.rating=order[position].rate.toFloat()

                            notifyItemChanged(position)
                            notifyDataSetChanged()

                        }

                        override fun onError(anError: ANError?) {
                            makeToast("Can't connect to server . Try again later")
                            makeToast(anError!!.errorBody)
                            view.lyt_book_details.animation =
                                AnimationUtils.loadAnimation(context, R.anim.item_animation_fallup)
                            view.lyt_book_details.visibility = View.GONE
                        }

                    })
            }

            view.btnDeleteOrder.setOnClickListener {
                deleteOrder(order[position].id, holder.adapterPosition)
            }

            view.btnBackOrder.setOnClickListener {
                view.lyt_book_details.animation =
                    AnimationUtils.loadAnimation(context, R.anim.item_animation_fallup)
                view.lyt_book_details.visibility = View.GONE

            }

            view.ratingBar.setOnRatingBarChangeListener { ratingBar, v, b -> }
            view.ratingBar.rating = order[position].rate.toFloat()
        }

    }


    private fun deleteOrder(orderID: String, position: Int) {
        val pref = Preference(context!!)
        AndroidNetworking.post(URL.BASE_URL + "/api/user/daftarpemesanan/$orderID/delete")
            .addHeaders("user_id", pref.getPrefString(prefConstant.USER_ID))
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject?) {
                    makeToast("Data Order Deleted !")
                    order.removeAt(position)
                    view.recyclerViewOrder.removeViewAt(position)
                    notifyDataSetChanged()
                    notifyItemRemoved(position);
                    view.lyt_book_details.animation =
                        AnimationUtils.loadAnimation(context, R.anim.item_animation_fallup)
                    view.lyt_book_details.visibility = View.GONE

                }

                override fun onError(anError: ANError?) {
                    makeToast("Failed Delete Booking Order")
                    view.lyt_book_details.animation =
                        AnimationUtils.loadAnimation(context, R.anim.item_animation_fallup)
                    view.lyt_book_details.visibility = View.GONE
                }

            })
    }

    fun makeToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun fromHtml(html: String?): Spanned? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY)
        } else {
            Html.fromHtml(html)
        }
    }
}

class orderBasicHolder(view: View) : RecyclerView.ViewHolder(view) {
    val tvOrderID = view.orderID
    val tvOrderRoomName = view.orderRoomName
    val tvOrderPax = view.orderRoomCapacity
    val ivRoomThumbnail = view.orderRoomThumbnail
    val tvStart = view.orderRoomStart
    val tvEnd = view.orderRoomEnd
    val tvStatus = view.orderBookStatus
    val containerStatus = view.orderStatusColor
    val tvRoomRatingIndicator = view.rateInt
}