package com.example.hotsliceapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AdapterListeHome(private val listaProdotti:List<Item>):
    RecyclerView.Adapter<AdapterListeHome.MyViewHolder>() {

    class MyViewHolder(itemView : View):RecyclerView.ViewHolder(itemView){

        val nomeProdotto : TextView = itemView.findViewById(R.id.nomeItem)
        val prezzoProdotto: TextView = itemView.findViewById(R.id.prezzoItem)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.each_item, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listaProdotti.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item : Item = listaProdotti[position]
        holder.nomeProdotto.text = item.nome
        holder.prezzoProdotto.text = "${item.prezzo} €"
    }


}