package com.senjapagi.meetup.ModelAdapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.senjapagi.meetup.Preference.URL
import com.senjapagi.meetup.R
import com.senjapagi.meetup.room_details
import com.senjapagi.shsd.Preference.Preference
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.list_room.view.*


class room_adapter(private val room:ArrayList<model_room>,val context:Context,var accesser:String,
var start:String,var end:String)
    :RecyclerView.Adapter<roomHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): roomHolder {
  //      TODO("Not yet implemented")
        return roomHolder(LayoutInflater.from(context).
        inflate(R.layout.list_room,parent,false))
    }

    override fun getItemCount(): Int {
  //      TODO("Not yet implemented")
        return room.size
    }

    override fun onBindViewHolder(holder: roomHolder, position: Int) {
   //     TODO("Not yet implemented")
        holder.tvRoomName.text = room.get(position).name
        holder.tvRoomCapacity.text = room.get(position).capacity
        holder.tvRoomID.text = room.get(position).id
        holder.tvBookedCount.text = room.get(position).bookCount
        val thumbnail = room.get(position).thumbnail
        Picasso.get()
            .load(URL.ROOM_THUMBNAIL+thumbnail)
            .memoryPolicy(MemoryPolicy.NO_CACHE)
            .networkPolicy(NetworkPolicy.NO_CACHE)
            .into(holder.ivRoomThumbnail)

        val rating = room.get(position).rating
        val iRating = rating.toInt()
        when(iRating){
             1 ->holder.tvRoomRating.text = "⭐"
            in 1..2->holder.tvRoomRating.text = "⭐⭐"
            2 ->holder.tvRoomRating.text = "⭐⭐"
            in 2..3->holder.tvRoomRating.text = "⭐⭐⭐"
            3 ->holder.tvRoomRating.text = "⭐⭐⭐"
            in 3..4->holder.tvRoomRating.text = "⭐⭐⭐"
            4 ->holder.tvRoomRating.text = "⭐⭐⭐⭐"
            in 4..5->holder.tvRoomRating.text = "⭐⭐⭐⭐⭐"
            5 ->holder.tvRoomRating.text = "⭐⭐⭐⭐⭐"
            else->holder.tvRoomRating.text = "Not Rated Yet"
        }

        holder.itemView.setOnClickListener {
            Toast.makeText(context,room.get(position).name, Toast.LENGTH_LONG).show()
            val a = Intent(context,room_details::class.java).apply {
                putExtra("id",room.get(position).id)
                putExtra("desc",room.get(position).description)
                putExtra("thumbnails",thumbnail)
                putExtra("order_pax", room[position].orderPax)
                putExtra("pax",room.get(position).capacity)
                putExtra("name",room.get(position).name)
                putExtra("accesser",accesser)
                putExtra("start",start)
                putExtra("end",end)

                val preference = Preference(context)
                preference.saveTempRoom(room.get(position).id)
            }
            context.startActivity(a)
        }

    }


}

class roomHolder(view:View) : RecyclerView.ViewHolder(view){
    val tvRoomID = view.IDroom
    val tvRoomName = view.roomName
    val tvRoomCapacity = view.roomCapacity
    val ivRoomThumbnail = view.roomThumbnail
    val tvBookedCount = view.roomBookedCount
    val tvRoomRating = view.rateBar
}