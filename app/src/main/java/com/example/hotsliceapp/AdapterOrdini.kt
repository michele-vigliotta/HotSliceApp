package com.example.hotsliceapp

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AdapterOrdini(
    private val ordiniList: List<ItemOrdine>,
    private val isStaff: Boolean
):
    RecyclerView.Adapter<AdapterOrdini.OrdiniViewHolder>() {

    var onItemClick: ((ItemOrdine) -> Unit)? = null
    // ViewHolder per la RecyclerView
    class OrdiniViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val descrizioneTextView: TextView = view.findViewById(R.id.descrizioneTextView)
        val statoTextView: TextView = view.findViewById(R.id.statoTextView)
        val dataTextView: TextView = view.findViewById(R.id.dataTextView)
        val tipoTextView: TextView = view.findViewById(R.id.tipoTextView)
        val tavoloOrarioTextView: TextView = view.findViewById(R.id.tavoloOrarioTextView)
        val nomeTextView: TextView = view.findViewById(R.id.nomeTextView)
        val telefonoTextView: TextView = view.findViewById(R.id.telefonoTextView)
        val totaleTextView: TextView = view.findViewById(R.id.totaleTextView)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrdiniViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_ordine, parent, false)
        return OrdiniViewHolder(view)
    }


    override fun getItemCount(): Int = ordiniList.size

    override fun onBindViewHolder(holder: OrdiniViewHolder, position: Int) {
        val ordine = ordiniList[position]
        holder.dataTextView.text = "Ordine in data: " + ordine.data
        Log.d("Descrizione", "Descrizione: '${ordine.descrizione}'")
        // Pulizia della descrizione per evitare che vada a capo prima di "Totale ordine"
        val descrizionePulita = ordine.descrizione.trim().replace("\n", " ")
        holder.descrizioneTextView.text = "Descrizione: " + descrizionePulita
        holder.totaleTextView.text ="Totale ordine: ${ordine.totale}€"
        if (ordine.ora == "") {
            holder.tavoloOrarioTextView.text = "Tavolo: ${ordine.tavolo}"
            holder.tipoTextView.text = "Tipo: Ordine al tavolo"
        }
        else{
            holder.tavoloOrarioTextView.text = "Ora di ritiro: ${ordine.ora}"
            holder.tipoTextView.text = "Tipo: Ordine d'asporto"
        }
        if (isStaff && ordine.ora != "") {
            holder.nomeTextView.visibility = View.VISIBLE
            holder.nomeTextView.text = "Nome: ${ordine.nome}"

            holder.telefonoTextView.visibility = View.VISIBLE
            holder.telefonoTextView.text = "Telefono: ${ordine.telefono}"
        } else {
            holder.nomeTextView.visibility = View.GONE
            holder.telefonoTextView.visibility = View.GONE
        }
        holder.statoTextView.text = "Stato: " + ordine.stato
        holder.itemView.setOnClickListener {
            onItemClick?.invoke(ordine)
        }
    }
}