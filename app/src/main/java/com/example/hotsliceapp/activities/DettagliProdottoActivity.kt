package com.example.hotsliceapp.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.hotsliceapp.CarrelloViewModel
import com.example.hotsliceapp.Item
import com.example.hotsliceapp.ItemCarrello
import com.example.hotsliceapp.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import com.google.firebase.firestore.firestore
import org.checkerframework.common.returnsreceiver.qual.This

class DettagliProdottoActivity : AppCompatActivity() {

    private val carrelloViewModel: CarrelloViewModel by viewModels()
    private val RESULT_CODE_CARRELLO = 200
    private var listaCarrello = arrayListOf<ItemCarrello>()
    private lateinit var auth: FirebaseAuth
    val db = Firebase.firestore
    lateinit var role: String


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
        val layoutContainer = findViewById<LinearLayout>(R.id.linearLayout2)
        val layoutDettagli = findViewById<ConstraintLayout>(R.id.layoutDettagli)
        val buttonAddToCart : Button = findViewById(R.id.button_add_to_cart)
        val favButton = findViewById<CheckBox>(R.id.favButton)

        auth = Firebase.auth
        val authid = (auth.currentUser?.uid).toString()
        val documentSnapshot = db.collection("users").document(authid)
        documentSnapshot.get().addOnSuccessListener {
                document ->
            role = document.getString("role").toString()
            if (role == "staff") {
                layoutContainer.removeAllViews()
                layoutDettagli.removeView(favButton)
                buttonAddToCart.text = "Modifica prodotto"
            }


        }


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
            val currentQuantity = editTextQuantity.text.toString().toInt()
            if (currentQuantity > 0) {
                if (item != null) {
                    addToCart(item.nome, item.foto, item.prezzo, currentQuantity)
                }
            }
        }

        if (item?.nome != null) {
            val controllopreferito = db.collection("preferiti")
                .whereEqualTo("nome", item?.nome)
                .whereEqualTo("userId", authid)
                .get()

            // Rimuovi il listener precedente per evitare problemi di riciclo
            favButton.setOnCheckedChangeListener(null)

            controllopreferito.addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    favButton.isChecked = true
                } else {
                    favButton.isChecked = false
                }}}



                favButton.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        addToFavorites(item)
                    } else {
                        removeFromFavorites(item)
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
                val items = carrelloViewModel.getItems()

                Toast.makeText(this, "Aggiunto al carrello", Toast.LENGTH_SHORT).show()

            }


            private fun addToFavorites(item: Item?) {
                val authid = (auth.currentUser?.uid).toString()
                if (item?.nome != null) {
                    val controllopreferito = db.collection("preferiti")
                        .whereEqualTo("nome", item.nome)
                        .whereEqualTo("userId", authid)
                        .get()
                val nuovoPreferito = hashMapOf(
                    "userId" to authid,
                    "nome" to item.nome
                )
                controllopreferito.addOnSuccessListener { documents ->
                    if (documents.isEmpty) {
                        val userFavoritesRef = db.collection("preferiti")
                        userFavoritesRef.add(nuovoPreferito)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Prodotto aggiunto ai preferiti!", Toast.LENGTH_SHORT)
                                    .show()
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "Errore durante l'inserimento!", Toast.LENGTH_SHORT)
                                    .show()
                            }
                    }
                }

            }}


            private fun removeFromFavorites(item: Item?) {
                val authid = (auth.currentUser?.uid).toString()
                //Toast.makeText(this, "Fa qualcosa", Toast.LENGTH_SHORT).show()
                db.collection("preferiti")
                    .whereEqualTo("userId", authid)
                    .whereEqualTo("nome", item?.nome)
                    .get()

                    .addOnSuccessListener { documents ->
                        for (document in documents) {
                            db.collection("preferiti").document(document.id).delete()
                                .addOnSuccessListener {
                                    Toast.makeText(
                                        this,
                                        "Prodotto rimosso dai preferiti!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(
                                        this,
                                        "Errore durante la rimozione!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                        }
                    }
                    .addOnFailureListener { e ->
                        // Gestisci l'errore
                        Toast.makeText(
                            this,
                            "Errore nella ricerca del documento",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
        }








