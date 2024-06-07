package com.example.hotsliceapp.activities

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.hotsliceapp.Item
import com.example.hotsliceapp.R
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

class DettagliProdottoActivity : AppCompatActivity() {
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
        var editTextQuantity : EditText = findViewById(R.id.editText_quantity)
        var buttonMinus : Button = findViewById(R.id.button_minus)
        var buttonAdd : Button = findViewById(R.id.button_plus)
        var buttonAddToCart : Button = findViewById(R.id.button_add_to_cart)

        editTextQuantity.setText("0")

            buttonMinus.setOnClickListener {
                var currentQuantity = editTextQuantity.text.toString().toInt()
                if (currentQuantity > 0) {
                currentQuantity--
                editTextQuantity.setText(currentQuantity.toString())
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
                        addToCart(item.nome, currentQuantity)
                    }
                }
            }
        }
    }


    private fun addToCart(itemName: String, quantity: Int) {
        Toast.makeText(this, "Item added to cart: $itemName", Toast.LENGTH_SHORT).show()
    }
}
