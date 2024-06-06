package com.example.hotsliceapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage

class AdapterListeHome(private val listaProdotti:List<Item>):  //estende Adapter
    RecyclerView.Adapter<AdapterListeHome.MyViewHolder>() {

    class MyViewHolder(itemView : View):RecyclerView.ViewHolder(itemView){

        val nomeProdotto : TextView = itemView.findViewById(R.id.nomeItem)
        val prezzoProdotto: TextView = itemView.findViewById(R.id.prezzoItem)
        val imageProdotto: ImageView = itemView.findViewById(R.id.imageViewItem)
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

        //riferimento a firebase storage
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference



        /*
        if (!item.foto.isNullOrEmpty()) {
            //riferimento all'immagine specifica
            val imageRef = storageRef.child("images/${item.foto}")

            imageRef.downloadUrl.addOnSuccessListener { uri ->
                Glide.with(holder.itemView.context)
                    .load(uri)
                    .into(holder.imageProdotto)
            }.addOnFailureListener {
                // Imposta l'immagine di fallback in caso di errore
                holder.imageProdotto.setImageResource(R.drawable.pizza_foto)
            }
        } else {
            // Se il campo foto è vuoto o nullo, imposta l'immagine di fallback
            holder.imageProdotto.setImageResource(R.drawable.pizza_foto)
        }
        */
    }


}