package com.example.hotsliceapp

import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AdapterListeHome(private val listaProdotti:List<Item>):  //estende Adapter
    RecyclerView.Adapter<AdapterListeHome.MyViewHolder>() {

        var onItemClick : ((Item) -> Unit)? = null

    class MyViewHolder(itemView : View):RecyclerView.ViewHolder(itemView){

        val immagineProdotto : ImageView = itemView.findViewById(R.id.imageViewItem)
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

        holder.immagineProdotto.setImageResource(item.image)
        holder.nomeProdotto.text = item.nome
        holder.prezzoProdotto.text = "${item.prezzo} €"

        holder.itemView.setOnClickListener{
            onItemClick?.invoke(item)
        }
    }


}