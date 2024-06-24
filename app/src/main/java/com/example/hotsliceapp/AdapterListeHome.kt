package com.example.hotsliceapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import java.util.Locale

class AdapterListeHome(private var listaProdotti:List<Item>):  //estende Adapter
    RecyclerView.Adapter<AdapterListeHome.MyViewHolder>() {

    var onItemClick: ((Item) -> Unit)? = null
    class MyViewHolder(itemView : View): RecyclerView.ViewHolder(itemView){

        val nomeProdotto : TextView = itemView.findViewById(R.id.nomeItem)
        val prezzoProdotto: TextView = itemView.findViewById(R.id.prezzoItem)
        val imageProdotto: ImageView = itemView.findViewById(R.id.imageViewItem)
        val loadingBar: ProgressBar = itemView.findViewById(R.id.progressBarFoto)
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

        holder.itemView.setOnClickListener{
            onItemClick?.invoke(item)              //lamba function, definita nel fragment
        }


        //se l'item ha una foto la carica
        if(!item.foto.isNullOrEmpty()) {
            val storageReference =
                FirebaseStorage.getInstance().reference.child("${item.foto}")
            storageReference.downloadUrl.addOnSuccessListener { uri ->
                Picasso.get().load(uri).into(holder.imageProdotto, object : com.squareup.picasso.Callback {
                        override fun onSuccess() {
                            holder.loadingBar.visibility = View.GONE
                            holder.imageProdotto.visibility = View.VISIBLE
                        }

                        override fun onError(e: Exception?) {
                            holder.imageProdotto.setImageResource(R.drawable.pizza_foto)
                            holder.loadingBar.visibility = View.GONE
                            holder.imageProdotto.visibility = View.VISIBLE
                        }

                    })
            }.addOnFailureListener {
                holder.imageProdotto.setImageResource(R.drawable.pizza_foto)
                holder.loadingBar.visibility = View.GONE
                holder.imageProdotto.visibility = View.VISIBLE
            }
        }else{
            holder.imageProdotto.setImageResource(R.drawable.pizza_foto)
            holder.loadingBar.visibility = View.GONE
            holder.imageProdotto.visibility = View.VISIBLE
        }
    }
    fun setFilteredList(listaProdotti: List<Item>){
        this.listaProdotti = listaProdotti
        notifyDataSetChanged()
    }

    fun updateList(newList: List<Item>) {
        listaProdotti = newList
        notifyDataSetChanged()
    }

}
