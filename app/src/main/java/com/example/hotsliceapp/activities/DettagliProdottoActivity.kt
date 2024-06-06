package com.example.hotsliceapp.activities

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.ComponentActivity
import com.example.hotsliceapp.Item
import com.example.hotsliceapp.R

class DettagliProdottoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dettagliprodotto)

        val item = intent.getParcelableExtra<Item>("item")
        if(item != null){
            val textView : TextView = findViewById(R.id.textViewDettagli)
            val imageView : ImageView = findViewById(R.id.imageViewDettagli)

            textView.text = item.nome
            imageView.setImageResource(item.image)
        }

    }
}
