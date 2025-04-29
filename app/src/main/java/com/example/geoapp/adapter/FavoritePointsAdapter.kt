package com.example.geoapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.geoapp.R
import com.example.geoapp.models.FavoritePoint

class FavoritePointsAdapter(
    private val points: List<FavoritePoint>,
    private val onNavigateToMap: (FavoritePoint) -> Unit
) : RecyclerView.Adapter<FavoritePointsAdapter.FavoritePointViewHolder>() {

    class FavoritePointViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtName: TextView = view.findViewById(R.id.txtPointName)
        val btnNavigate: Button = view.findViewById(R.id.btnNavigate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoritePointViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_favorite_point, parent, false)
        return FavoritePointViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavoritePointViewHolder, position: Int) {
        val point = points[position]
        holder.txtName.text = point.name
        holder.btnNavigate.setOnClickListener { onNavigateToMap(point) }
    }

    override fun getItemCount(): Int = points.size
}