package com.example.hotsliceapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PizzaAdapter(private val listaPizze:List<Item>):
    RecyclerView.Adapter<PizzaAdapter.MyViewHolder>() {

    class MyViewHolder(itemView : View):RecyclerView.ViewHolder(itemView){

        val nomePizza : TextView = itemView.findViewById(R.id.nomeItem)
        val prezzoPizza: TextView = itemView.findViewById(R.id.prezzoItem)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.each_item, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listaPizze.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item : Item = listaPizze[position]
        holder.nomePizza.text = item.nome
        holder.prezzoPizza.text = "${item.prezzo} €"
    }


}