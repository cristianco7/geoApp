package com.example.geoapp.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.geoapp.R
import com.example.geoapp.models.FavoritePoint

class FavoritePointAdapter(
    private var points: List<FavoritePoint>
) : RecyclerView.Adapter<FavoritePointAdapter.FavoritePointViewHolder>() {

    class FavoritePointViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtPointName: TextView = view.findViewById(R.id.txtPointName)
        val txtCoordinates: TextView = view.findViewById(R.id.txtCoordinates)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoritePointViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_favorite_point_list, parent, false)
        return FavoritePointViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: FavoritePointViewHolder, position: Int) {
        val point = points[position]
        holder.txtPointName.text = point.name
        holder.txtCoordinates.text = "Lat: ${point.latitude}, Lng: ${point.longitude}"
    }

    override fun getItemCount(): Int = points.size

    fun updatePoints(newPoints: List<FavoritePoint>) {
        points = newPoints
        notifyDataSetChanged()
    }
}