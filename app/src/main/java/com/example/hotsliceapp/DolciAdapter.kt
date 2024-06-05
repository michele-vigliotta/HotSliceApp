package com.example.hotsliceapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DolciAdapter(private val listaDolci:List<Item>):
    RecyclerView.Adapter<DolciAdapter.ViewHolderDolci>()  {

    class ViewHolderDolci(itemView : View):RecyclerView.ViewHolder(itemView){

        val nomeDolce : TextView = itemView.findViewById(R.id.nomeItem)
        val prezzoDolce: TextView = itemView.findViewById(R.id.prezzoItem)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderDolci {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.each_item, parent, false)
        return DolciAdapter.ViewHolderDolci(view)
    }

    override fun getItemCount(): Int {
        return listaDolci.size
    }

    override fun onBindViewHolder(holder: DolciAdapter.ViewHolderDolci, position: Int) {
        val item : Item = listaDolci[position]
        holder.nomeDolce.text = item.nome
        holder.prezzoDolce.text = "${item.prezzo} €"
    }
}