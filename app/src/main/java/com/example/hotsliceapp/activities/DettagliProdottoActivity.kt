package com.example.hotsliceapp.activities

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.ComponentActivity
import com.bumptech.glide.Glide
import com.example.hotsliceapp.Item
import com.example.hotsliceapp.R
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

class DettagliProdottoActivity : ComponentActivity() {
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



    }
}
