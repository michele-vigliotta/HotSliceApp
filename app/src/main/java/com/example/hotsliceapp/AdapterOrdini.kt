package com.example.hotsliceapp

import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class AdapterOrdini(
    private val ordiniList: List<ItemOrdine>,
    private val isStaff: Boolean
) : RecyclerView.Adapter<AdapterOrdini.OrdiniViewHolder>() {

    var onItemClick: ((ItemOrdine) -> Unit)? = null

    // ViewHolder per la RecyclerView
    @RequiresApi(Build.VERSION_CODES.O)
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

    class OrdiniViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val descrizioneTextView: TextView = view.findViewById(R.id.descrizioneTextView)
        val statoTextView: TextView = view.findViewById(R.id.textViewStatoOrdine)
        val dataTextView: TextView = view.findViewById(R.id.dataTextView)
        val tipoTextView: TextView = view.findViewById(R.id.tipoTextView)
        val tavoloOrarioTextView: TextView = view.findViewById(R.id.tavoloOrarioTextView)
        val nomeTextView: TextView = view.findViewById(R.id.nomeTextView)
        val telefonoTextView: TextView = view.findViewById(R.id.telefonoTextView)
        val totaleTextView: TextView = view.findViewById(R.id.totaleTextView)
        val imageViewStatoOrdine: ImageView = view.findViewById(R.id.imageViewStatoOrdine)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrdiniViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_ordine, parent, false)
        return OrdiniViewHolder(view)
    }

    override fun getItemCount(): Int = ordiniList.size

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: OrdiniViewHolder, position: Int) {
        val ordine = ordiniList[position]
        val ordineDateTime = LocalDateTime.parse(ordine.data, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        holder.dataTextView.text = "Ordine in data: " + ordineDateTime.format(formatter)
        Log.d("Descrizione", "Descrizione: '${ordine.descrizione}'")


        val descrizioneFormattata = formatOrderDescription(ordine.descrizione)
        holder.descrizioneTextView.text = "Descrizione: " + descrizioneFormattata

        holder.totaleTextView.text = "Totale ordine: ${ordine.totale} €"
        if (ordine.ora == "") {
            holder.tavoloOrarioTextView.text = "Tavolo: ${ordine.tavolo}"
            holder.tipoTextView.text = "Tipo: Servizio al tavolo"
        } else {
            holder.tavoloOrarioTextView.text = "Ora di ritiro: ${ordine.ora}"
            holder.tipoTextView.text = "Tipo: Servizio d'asporto"
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

        // Imposta l'immagine in base allo stato dell'ordine
        when (ordine.stato) {
            "in corso" -> {
                holder.imageViewStatoOrdine.setImageResource(R.drawable.clessidra)
                holder.imageViewStatoOrdine.visibility = View.VISIBLE
            }
            "Accettato" -> {
                holder.imageViewStatoOrdine.setImageResource(R.drawable.accettato)
                holder.imageViewStatoOrdine.visibility = View.VISIBLE
            }
            "Rifiutato" -> {
                holder.imageViewStatoOrdine.setImageResource(R.drawable.rifiutato)
                holder.imageViewStatoOrdine.visibility = View.VISIBLE
            }
            else -> {
                holder.imageViewStatoOrdine.visibility = View.GONE
            }
        }
    }
    // Funzione per formattare la descrizione dell'ordine
    private fun formatOrderDescription(orderDescription: String): String {

        val items = orderDescription.split(";").filter { it.isNotBlank() }
        val formattedItems = mutableListOf<String>()

        for (item in items) {

            val parts = item.split(",").map { it.trim() }
            if (parts.size == 2) {

                val quantityPart = parts[1].substringAfter(":").trim()
                val namePart = parts[0].substringAfter(":").trim()


                formattedItems.add("${quantityPart}x $namePart")
            }
        }
        return formattedItems.joinToString(", ")
    }
}
