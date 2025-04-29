package com.example.geoapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.material.card.MaterialCardView
import androidx.recyclerview.widget.RecyclerView
import com.example.geoapp.R
import com.example.geoapp.models.BaseMapItem

class BaseMapAdapter(
    private val items: List<BaseMapItem>,
    private val currentStyle: String,
    private val onItemClick: (BaseMapItem) -> Unit
) : RecyclerView.Adapter<BaseMapAdapter.BaseMapViewHolder>() {

    private var selectedPosition: Int = items.indexOfFirst { it.styleUrl == currentStyle }

    class BaseMapViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgBaseMap: ImageView = view.findViewById(R.id.imgBaseMap)
        val txtBaseMap: TextView = view.findViewById(R.id.txtBaseMap)
        val cardViewBasemap: MaterialCardView = view.findViewById(R.id.cardViewBaseMap)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseMapViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_basemap, parent, false)
        return BaseMapViewHolder(view)
    }

    override fun onBindViewHolder(holder: BaseMapViewHolder, position: Int) {
        val item = items[position]
        holder.imgBaseMap.setImageResource(item.imageRes)
        holder.txtBaseMap.text = item.title

        if (position == selectedPosition) {
            holder.cardViewBasemap.strokeColor =
                ContextCompat.getColor(holder.itemView.context, R.color.blue)
            holder.cardViewBasemap.strokeWidth = 6
        } else {
            holder.cardViewBasemap.strokeColor =
                ContextCompat.getColor(holder.itemView.context, android.R.color.transparent)
            holder.cardViewBasemap.strokeWidth = 0
        }

        holder.itemView.setOnClickListener {
            val currentPosition = holder.adapterPosition
            if (currentPosition == RecyclerView.NO_POSITION) return@setOnClickListener
            val previousPosition = selectedPosition
            selectedPosition = currentPosition
            notifyItemChanged(previousPosition)
            notifyItemChanged(selectedPosition)
            onItemClick(item)
        }
    }

    override fun getItemCount(): Int = items.size
}
