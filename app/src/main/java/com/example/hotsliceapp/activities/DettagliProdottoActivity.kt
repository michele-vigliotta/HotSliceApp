package com.example.hotsliceapp.activities

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.hotsliceapp.CarrelloViewModel
import com.example.hotsliceapp.CarrelloViewModelFactory
import com.example.hotsliceapp.Item
import com.example.hotsliceapp.ItemCarrello
import com.example.hotsliceapp.R
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

class DettagliProdottoActivity : AppCompatActivity() {

    private lateinit var CarrelloViewModel: CarrelloViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dettagliprodotto)

        val item = intent.getParcelableExtra<Item>("item")
        //CarrelloViewModel = ViewModelProvider(this, CarrelloViewModelFactory(application)).get(CarrelloViewModel::class.java)
        //CarrelloViewModel = ViewModelProvider(this).get(CarrelloViewModel::class.java)
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



        buttonMinus.setOnClickListener {
                var currentQuantity = editTextQuantity.text.toString().toInt()
                if (currentQuantity > 0) {
                    currentQuantity--
                    editTextQuantity.setText(currentQuantity.toString())
                }
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

        Toast.makeText(this, "Item: $itemName, Quantity: $quantity", Toast.LENGTH_SHORT).show()
        /*
        val itemCarrello = ItemCarrello(itemName, foto, prezzo, quantity)
        CarrelloViewModel.addItem(itemCarrello)
        for (item in CarrelloViewModel.itemsCarrello.value ?: emptyList()) {
            Toast.makeText(this, "Item: ${item.nome}, Quantity: ${item.quantita}", Toast.LENGTH_SHORT).show()

        }
        */

    }
    }




