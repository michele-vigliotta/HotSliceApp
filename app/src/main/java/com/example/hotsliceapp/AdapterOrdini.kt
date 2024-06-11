package com.example.hotsliceapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AdapterOrdini(private val ordiniList: List<ItemOrdine>):
    RecyclerView.Adapter<AdapterOrdini.OrdiniViewHolder>() {

    // ViewHolder per la RecyclerView
    class OrdiniViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val descrizioneTextView: TextView = view.findViewById(R.id.descrizioneTextView)
        val statoTextView: TextView = view.findViewById(R.id.statoTextView)
        val dataTextView: TextView = view.findViewById(R.id.dataTextView) // Aggiungi TextView per la data

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrdiniViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_ordine, parent, false)
        return OrdiniViewHolder(view)
    }


    override fun getItemCount(): Int = ordiniList.size

    override fun onBindViewHolder(holder: OrdiniViewHolder, position: Int) {
        val ordine = ordiniList[position]
        holder.descrizioneTextView.text = "Descrizione ordine:\n${ordine.descrizione}"
        holder.statoTextView.text = "Stato: " + ordine.stato
        holder.dataTextView.text = "Data: " + ordine.data
    }
}