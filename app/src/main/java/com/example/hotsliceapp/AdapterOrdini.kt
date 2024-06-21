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
        val tipoTextView: TextView = view.findViewById(R.id.textViewTipo)
        val tavoloOrarioTextView: TextView = view.findViewById(R.id.textViewTavoloOra)
        val nomeTextView: TextView = view.findViewById(R.id.textViewNome)
        val telefonoTextView: TextView = view.findViewById(R.id.textViewTelefono)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrdiniViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_ordine, parent, false)
        return OrdiniViewHolder(view)
    }


    override fun getItemCount(): Int = ordiniList.size

    override fun onBindViewHolder(holder: OrdiniViewHolder, position: Int) {
        val ordine = ordiniList[position]
        holder.dataTextView.text = "Ordine in data: " + ordine.data
        holder.descrizioneTextView.text = "Descrizione:\n${ordine.descrizione}" +
                "Totale ordine: ${ordine.totale}€"
        //holder.tipoTextView.text = "Tipo: ${ordine.tipo}"
        if (ordine.ora == "") {
            holder.tavoloOrarioTextView.text = "Tavolo: ${ordine.tavolo}"
        }
        else{
            holder.tavoloOrarioTextView.text = "Ora di ritiro: ${ordine.ora}"
        }
        //holder.nomeTextView.text = "Nome: ${ordine.nome}"
        //holder.telefonoTextView.text = "Telefono: ${ordine.telefono}"
        holder.statoTextView.text = "Stato: " + ordine.stato
        holder.itemView.setOnClickListener {
            onItemClick?.invoke(ordine)
        }
    }
}