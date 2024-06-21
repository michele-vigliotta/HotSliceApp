package com.example.hotsliceapp.fragments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.hotsliceapp.R

class AdapterStatistiche : RecyclerView.Adapter<AdapterStatistiche.ViewHolder>() {

    private var productCount: Map<String, Int> = mapOf()

    fun setData(data: Map<String, Int>) {
        productCount = data
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_statistica, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = productCount.keys.toList()[position]
        val count = productCount[product]
        holder.productName.text = product
        holder.productCount.text = count.toString()
    }

    override fun getItemCount(): Int {
        return productCount.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productName: TextView = itemView.findViewById(R.id.productName)
        val productCount: TextView = itemView.findViewById(R.id.productCount)
    }
}