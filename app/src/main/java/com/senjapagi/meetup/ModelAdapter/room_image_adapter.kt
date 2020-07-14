package com.senjapagi.meetup.ModelAdapter

import android.content.Context
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.senjapagi.meetup.Preference.URL
import com.senjapagi.meetup.R
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.list_foto.view.*


class room_image_adapter(private val roomImageList: ArrayList<model_room_image>, val context: Context) :
    RecyclerView.Adapter<roomImageHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): roomImageHolder {
        //       TODO("Not yet implemented")
        return roomImageHolder(
            LayoutInflater.from(context).inflate(R.layout.list_foto, parent, false)
        )
    }

    override fun getItemCount(): Int {
        //     TODO("Not yet implemented")
        return roomImageList.size
    }

    override fun onBindViewHolder(holder: roomImageHolder, position: Int) {
        //    TODO("Not yet implemented")
        val link = roomImageList.get(position).imageLink
        val id = roomImageList.get(position).id
        Picasso.get()
            .load(URL.ROOM_PHOTOS+link)
            .memoryPolicy(MemoryPolicy.NO_CACHE)
            .networkPolicy(NetworkPolicy.NO_CACHE)
            .into(holder.roomImage)

        holder.roomImage.setOnClickListener(){

        }


    }

}


class roomImageHolder(view: View) : RecyclerView.ViewHolder(view) {
    val roomImage = view.roomImage


}

