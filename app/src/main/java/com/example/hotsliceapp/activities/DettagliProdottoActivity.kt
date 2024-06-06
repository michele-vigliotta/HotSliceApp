package com.example.hotsliceapp.activities

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.ComponentActivity
import com.bumptech.glide.Glide
import com.example.hotsliceapp.Item
import com.example.hotsliceapp.R
import com.squareup.picasso.Picasso

class DettagliProdottoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dettagliprodotto)

        val item = intent.getParcelableExtra<Item>("item")
        if(item != null){
            val textView : TextView = findViewById(R.id.textViewDettagli)
            val imageView : ImageView = findViewById(R.id.imageViewDettagli)

            textView.text = item.nome

            if(!item.foto.isNullOrEmpty()){
            item.let {
                Glide.with(this)
                    .load(it.foto)
                    .into(imageView)

                textView.text = it.nome
            }

            } else {
                imageView.setImageResource(R.drawable.pizza_foto)
            }

        }

    }
}
