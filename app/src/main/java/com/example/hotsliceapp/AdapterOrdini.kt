package com.example.hotsliceapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AdapterOrdini(private val ordiniList: List<ItemOrdine>):
    RecyclerView.Adapter<AdapterOrdini.OrdiniViewHolder>() {

    var onItemClick: ((ItemOrdine) -> Unit)? = null
    // ViewHolder per la RecyclerView
    class OrdiniViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val descrizioneTextView: TextView = view.findViewById(R.id.descrizioneTextView)
        val statoTextView: TextView = view.findViewById(R.id.statoTextView)
        val dataTextView: TextView = view.findViewById(R.id.dataTextView)
        val dettaglioExtraTextView: TextView = view.findViewById(R.id.dettaglioExtraTextView)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrdiniViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_ordine, parent, false)
        return OrdiniViewHolder(view)
    }


    override fun getItemCount(): Int = ordiniList.size

    override fun onBindViewHolder(holder: OrdiniViewHolder, position: Int) {
        val ordine = ordiniList[position]
        if (ordine.ora == "") {
            holder.descrizioneTextView.text = "Descrizione ordine:\n${ordine.descrizione}\n" +
                    "Totale ordine: ${ordine.totale}€"
            holder.dettaglioExtraTextView.text = "Tavolo Numero: ${ordine.tavolo}"
        }
        else{
            holder.descrizioneTextView.text = "Descrizione ordine:\n${ordine.descrizione}\n" +
                    "Totale ordine: ${ordine.totale}€"
            holder.dettaglioExtraTextView.text = "Ora di ritiro: ${ordine.ora}"
        }
        holder.statoTextView.text = "Stato: " + ordine.stato
        holder.dataTextView.text = "Data: " + ordine.data

        holder.itemView.setOnClickListener {
            onItemClick?.invoke(ordine)
        }
    }
}