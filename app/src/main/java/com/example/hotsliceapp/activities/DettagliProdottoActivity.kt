package com.example.hotsliceapp.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.example.hotsliceapp.CarrelloViewModel
import com.example.hotsliceapp.Item
import com.example.hotsliceapp.ItemCarrello
import com.example.hotsliceapp.R
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

class DettagliProdottoActivity : AppCompatActivity() {

    private val carrelloViewModel: CarrelloViewModel by viewModels()
    private val RESULT_CODE_CARRELLO = 200
    private var listaCarrello = arrayListOf<ItemCarrello>()
    override fun onBackPressed() {

        returnResult() // Chiama la tua funzione per restituire il risultato
        super.onBackPressed()
    }

    private fun returnResult() {
        val intent = Intent()
        intent.putParcelableArrayListExtra("itemsCarrello", ArrayList(carrelloViewModel.getItems()))
        setResult(RESULT_CODE_CARRELLO, intent)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dettagliprodotto)


        val item = intent.getParcelableExtra<Item>("item")

            val textView : TextView = findViewById(R.id.textViewDettagli)
            val imageView : ImageView = findViewById(R.id.imageViewDettagli)

        if(item != null){
            textView.text = item.nome

            if(!item.foto.isNullOrEmpty()) {
                val storageReference = FirebaseStorage.getInstance().reference.child("${item.foto}")
                storageReference.downloadUrl.addOnSuccessListener { uri ->
                    Picasso.get().load(uri).into(imageView)
                }.addOnFailureListener{
                    imageView.setImageResource(R.drawable.pizza_foto)
                    }
            }
        } else {
                imageView.setImageResource(R.drawable.pizza_foto)
                }
        val editTextQuantity : EditText = findViewById(R.id.editText_quantity)
        val buttonMinus : Button = findViewById(R.id.button_minus)
        val buttonAdd : Button = findViewById(R.id.button_plus)
        val buttonAddToCart : Button = findViewById(R.id.button_add_to_cart)


/*
        buttonMinus.setOnClickListener {
                var currentQuantity = editTextQuantity.text.toString().toInt()
                if (currentQuantity > 0) {
                    currentQuantity--
                    editTextQuantity.setText(currentQuantity.toString())
                }
            } */

        buttonMinus.setOnClickListener {
            val items = carrelloViewModel.getItems() ?: emptyList()
            Toast.makeText(this, items.toString(), Toast.LENGTH_SHORT).show()
        }

            buttonAdd.setOnClickListener {
                var currentQuantity = editTextQuantity.text.toString().toInt()
                currentQuantity++
                editTextQuantity.setText(currentQuantity.toString())
                }

            buttonAddToCart.setOnClickListener {
                var currentQuantity = editTextQuantity.text.toString().toInt()
                if (currentQuantity > 0) {
                    if (item != null) {
                        addToCart(item.nome, item.foto, item.prezzo, currentQuantity)
                    }
                }
            }
        }
    private fun addToCart(itemName: String, foto: String?, prezzo: Double, quantity: Int) {
        val existingItem = listaCarrello.find { it.nome == itemName }
        if (existingItem != null) {
            existingItem.quantita += quantity
        } else {
            val newitemCarrello = ItemCarrello(itemName, foto, prezzo, quantity)
            listaCarrello.add(newitemCarrello)
        }


        carrelloViewModel.setItems(listaCarrello)
        var items = carrelloViewModel.getItems()

        Toast.makeText(this, " ${items}", Toast.LENGTH_SHORT).show()

        }


    }


private fun Intent.putParcelableArrayListExtra(s: String, arrayList: ArrayList<ItemCarrello>) {

}




