import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.hotsliceapp.ItemCarrello
import com.example.hotsliceapp.R
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

class AdapterCarrello(private var listaCarrello: MutableList<ItemCarrello>,
                      private val updateTotal: (Double) -> Unit)
    : RecyclerView.Adapter<AdapterCarrello.CarrelloViewHolder>() {

     var onGetList: ((List<ItemCarrello>) -> Unit)? = null

    class CarrelloViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewNome: TextView = itemView.findViewById(R.id.textViewNomeProdotto)
        val textViewPrezzo: TextView = itemView.findViewById(R.id.textViewPrezzo)
        val textViewQuantita: TextView = itemView.findViewById(R.id.textViewQuantitaProdotto)
        val imageViewProdotto: ImageView = itemView.findViewById(R.id.imageViewProdotto)
        val buttonPlus: Button = itemView.findViewById(R.id.buttonPlus)
        val buttonMinus: Button = itemView.findViewById(R.id.buttonMinus)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarrelloViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_carrello, parent, false)
        return CarrelloViewHolder(view)
    }

    override fun onBindViewHolder(holder: CarrelloViewHolder, position: Int) {
        val currentItem = listaCarrello[position]
        holder.textViewNome.text = currentItem.nome
        holder.textViewPrezzo.text = "${currentItem.prezzo} €"
        holder.textViewQuantita.text = currentItem.quantita.toString()
        holder.imageViewProdotto.setImageDrawable(null)
        if(!currentItem.foto.isNullOrEmpty()) {
            val storageReference = FirebaseStorage.getInstance().reference.child("${currentItem.foto}")
            storageReference.downloadUrl.addOnSuccessListener { uri ->
                Picasso.get().load(uri).into(holder.imageViewProdotto)
            }.addOnFailureListener{
                holder.imageViewProdotto.setImageResource(R.drawable.pizza_foto)
            }
        } else{
            holder.imageViewProdotto.setImageResource(R.drawable.pizza_foto)
        }
        holder.buttonPlus.setOnClickListener {
            currentItem.quantita++
            notifyItemChanged(position)
            updateTotal(calcolaTotale())
        }
        holder.buttonMinus.setOnClickListener {

            if (currentItem.quantita > 1) {
                currentItem.quantita--
                notifyItemChanged(position)
                updateTotal(calcolaTotale())
            } else if (currentItem.quantita == 1) {
                listaCarrello.removeAt(position)
                notifyItemRemoved(position)
                updateTotal(calcolaTotale())
            }

        }
    }

    private fun calcolaTotale(): Double {
        return listaCarrello.sumByDouble { it.quantita * it.prezzo }
    }

    override fun getItemCount(): Int {
        return listaCarrello.size
    }

    fun setData(listaCarrello: MutableList<ItemCarrello>) {
        this.listaCarrello = listaCarrello
        notifyDataSetChanged()
    }

    fun getList(callback:(List<ItemCarrello>) -> Unit)  {
        onGetList = callback
        onGetList?.invoke(listaCarrello)
    }

    fun clearList() {
        listaCarrello.clear()
        notifyDataSetChanged()
    }

}