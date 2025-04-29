package com.example.geoapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.geoapp.R
import com.example.geoapp.models.BaseMapItem

class BaseMapAdapter(
    private val items: List<BaseMapItem>,
    private val onItemClick: (BaseMapItem) -> Unit
) : RecyclerView.Adapter<BaseMapAdapter.BaseMapViewHolder>() {


    class BaseMapViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgBaseMap: ImageView = view.findViewById(R.id.imgBaseMap)
        val txtBaseMap: TextView = view.findViewById(R.id.txtBaseMap)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseMapViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_basemap, parent, false)
        return BaseMapViewHolder(view)
    }

    override fun onBindViewHolder(holder: BaseMapViewHolder, position: Int) {
        val item = items[position]
        holder.imgBaseMap.setImageResource(item.imageRes)
        holder.txtBaseMap.text = item.title
        holder.itemView.setOnClickListener { onItemClick(item) }
    }

    override fun getItemCount(): Int = items.size
}


