package com.example.hotsliceapp.activities

import FragmentModificaProdotto
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.example.hotsliceapp.CarrelloViewModel
import com.example.hotsliceapp.Item
import com.example.hotsliceapp.ItemCarrello
import com.example.hotsliceapp.R
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import com.google.firebase.firestore.firestore


class DettagliProdottoActivity : AppCompatActivity(), FragmentModificaProdotto.ModificaProdottoListener {

    private val carrelloViewModel: CarrelloViewModel by viewModels()
    private val RESULT_CODE_CARRELLO = 200
    private val RESULT_CODE_PRODOTTO_ELIMINATO = 201
    private var listaCarrello = arrayListOf<ItemCarrello>()
    private lateinit var auth: FirebaseAuth
    val db = Firebase.firestore
    lateinit var role: String
    lateinit var controllopreferito: Task<QuerySnapshot>

    private var isInternetConnected: Boolean = false
    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var networkCallback: ConnectivityManager.NetworkCallback

    private lateinit var progressBar: ProgressBar
    private lateinit var progressBarFoto: ProgressBar

    private lateinit var layoutDettagli: ConstraintLayout



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dettagliprodotto)

        layoutDettagli = findViewById(R.id.layoutDettagli)


        connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        registerNetworkCallback() // Registra il NetworkCallback all'avvio
        checkInternetConnection() // Verifica la connessione iniziale
    }

    private fun checkInternetConnection(){
        val networkInfo = connectivityManager.activeNetworkInfo
        isInternetConnected = networkInfo != null && networkInfo.isConnected

        if(isInternetConnected){
            layoutDettagli.visibility = View.VISIBLE

            val layoutContainer = findViewById<LinearLayout>(R.id.linearLayout2)
            val layoutDettagli = findViewById<ConstraintLayout>(R.id.layoutDettagli)
            val buttonAddToCart : Button = findViewById(R.id.button_add_to_cart)
            val favButton = findViewById<CheckBox>(R.id.favButton)
            val modificaButton = findViewById<Button>(R.id.button_nuovo)
            val textView : TextView = findViewById(R.id.textViewDettagli)
            val imageView : ImageView = findViewById(R.id.imageViewDettagli)
            val descrizione : TextView = findViewById(R.id.descrizioneDettagli)
            val prezzo : TextView = findViewById(R.id.prezzoProdotto)
            val editTextQuantity : EditText = findViewById(R.id.editText_quantity)
            val buttonMinus : Button = findViewById(R.id.button_minus)
            val buttonAdd : Button = findViewById(R.id.button_plus)

            val item = intent.getParcelableExtra<Item>("item")
            val prodotto = intent.getStringExtra("prodotto")

            progressBar = findViewById<ProgressBar>(R.id.progressBar)
            progressBarFoto = findViewById<ProgressBar>(R.id.progressBarFoto)
            val layoutFoto = findViewById<FrameLayout>(R.id.imageFrameLayout)
            imageView.visibility = View.GONE
            layoutDettagli.visibility = View.GONE

            auth = Firebase.auth
            val authid = (auth.currentUser?.uid).toString()
            val documentSnapshot = db.collection("users").document(authid)
            documentSnapshot.get().addOnSuccessListener {
                    document ->
                role = document.getString("role").toString()

                //Configurazione vista per staff
                if (role == "admin") {
                    layoutContainer.removeAllViews()
                    layoutDettagli.removeView(favButton)
                    modificaButton.visibility = Button.VISIBLE
                    buttonAddToCart.text = "Elimina Prodotto"

                    buttonAddToCart.setOnClickListener {
                        val builder = AlertDialog.Builder(this)
                            .setTitle("Conferma eliminazione")
                            .setMessage("Sei sicuro di voler eliminare questo prodotto?")
                            .setPositiveButton("Elimina", null) // Imposta il listener a null per ora
                            .setNegativeButton("Annulla") { dialog, _ -> dialog.dismiss() }

                        val dialog = builder.create() // Crea il dialog
                        dialog.setOnShowListener {
                            val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                            positiveButton.setOnClickListener {
                                eliminaProdotto(item?.nome.toString(), prodotto.toString())
                            }
                            dialog.getButton(DialogInterface.BUTTON_POSITIVE)
                                .setTextColor(ContextCompat.getColor(this, R.color.red))
                            dialog.getButton(DialogInterface.BUTTON_NEGATIVE)
                                .setTextColor(ContextCompat.getColor(this, R.color.red))
                        }
                        dialog.show()
                    }

                    modificaButton.setOnClickListener {
                        val dialog = FragmentModificaProdotto()
                        dialog.setModificaProdottoListener(this)
                        val bundle = Bundle()
                        bundle.putParcelable("item", item)
                        bundle.putString("prodotto", prodotto)
                        dialog.arguments = bundle
                        dialog.show(supportFragmentManager, "NuovoProdotto")
                    }
                    progressBar.visibility = View.GONE
                    layoutDettagli.visibility = View.VISIBLE
                }else if(role == "staff" && prodotto.toString() == "offerta") {
                    layoutContainer.removeAllViews()
                    layoutDettagli.removeView(favButton)
                    modificaButton.visibility = Button.VISIBLE
                    buttonAddToCart.text = "Elimina Prodotto"

                    buttonAddToCart.setOnClickListener {
                        val builder = AlertDialog.Builder(this)
                            .setTitle("Conferma eliminazione")
                            .setMessage("Sei sicuro di voler eliminare questo prodotto?")
                            .setPositiveButton("Elimina", null) // Imposta il listener a null per ora
                            .setNegativeButton("Annulla") { dialog, _ -> dialog.dismiss() }

                        val dialog = builder.create() // Crea il dialog
                        dialog.setOnShowListener {
                            val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                            positiveButton.setOnClickListener {
                                eliminaProdotto(item?.nome.toString(), prodotto.toString())
                            }
                            dialog.getButton(DialogInterface.BUTTON_POSITIVE)
                                .setTextColor(ContextCompat.getColor(this, R.color.red))
                            dialog.getButton(DialogInterface.BUTTON_NEGATIVE)
                                .setTextColor(ContextCompat.getColor(this, R.color.red))
                        }
                        dialog.show()
                    }

                    modificaButton.setOnClickListener {
                        val dialog = FragmentModificaProdotto()
                        dialog.setModificaProdottoListener(this)
                        val bundle = Bundle()
                        bundle.putParcelable("item", item)
                        bundle.putString("prodotto", prodotto)
                        dialog.arguments = bundle
                        dialog.show(supportFragmentManager, "NuovoProdotto")
                    }
                    progressBar.visibility = View.GONE
                    layoutDettagli.visibility = View.VISIBLE


                }else if(role == "staff"){
                    layoutContainer.removeAllViews()
                    layoutDettagli.removeView(favButton)
                    progressBar.visibility = View.GONE
                    layoutDettagli.visibility = View.VISIBLE
                    buttonAddToCart.visibility = View.GONE
                    progressBar.visibility = View.GONE
                    layoutDettagli.visibility = View.VISIBLE
                }else{
                    progressBar.visibility = View.GONE
                    layoutDettagli.visibility = View.VISIBLE
                }

            }

            //Configurazione vista dettagli per ogni utente
            if(item != null){
                textView.text = item.nome
                descrizione.text = item.descrizione
                prezzo.text = item.prezzo.toString()+"€"

                if (!item.foto.isNullOrEmpty()) {
                    val storageReference = FirebaseStorage.getInstance().reference.child("${item.foto}")
                    storageReference.downloadUrl.addOnSuccessListener { uri ->
                        Picasso.get().load(uri).into(imageView, object : com.squareup.picasso.Callback {
                            override fun onSuccess() {
                                progressBarFoto.visibility = View.GONE
                                imageView.visibility = View.VISIBLE
                            }

                            override fun onError(e: Exception?) {
                                imageView.setImageResource(R.drawable.pizza_foto)
                                progressBarFoto.visibility = View.GONE
                                imageView.visibility = View.VISIBLE
                            }
                        })
                    }.addOnFailureListener {
                        imageView.setImageResource(R.drawable.pizza_foto)
                        progressBarFoto.visibility = View.GONE
                        imageView.visibility = View.VISIBLE
                    }
                } else {
                    imageView.setImageResource(R.drawable.pizza_foto)
                    progressBarFoto.visibility = View.GONE
                    imageView.visibility = View.VISIBLE
                }
            } else {
                imageView.setImageResource(R.drawable.pizza_foto)
                progressBarFoto.visibility = View.GONE
                imageView.visibility = View.VISIBLE
            }



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
                controllopreferito = db.collection("preferiti")
                    .whereEqualTo("nome", item?.nome)
                    .whereEqualTo("userId", authid)
                    .get()

                // Rimuove il listener precedente se presente
                favButton.setOnCheckedChangeListener(null)

                controllopreferito.addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        favButton.isChecked = true
                    } else {
                        favButton.isChecked = false
                    }
                }
            }
            favButton.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    addToFavorites(item)
                } else {
                    removeFromFavorites(item)
                }
            }
        }else{
            // Reindirizza alla MainActivity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // Chiudi l'activity corrente
        }
    }

    private fun registerNetworkCallback() {
        networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {

                runOnUiThread {
                    checkInternetConnection()
                }
            }

            override fun onLost(network: Network) {

                runOnUiThread {
                    checkInternetConnection()
                }
            }
        }
        connectivityManager.registerDefaultNetworkCallback(networkCallback)
    }

    override fun onDestroy() {
        super.onDestroy()
        connectivityManager.unregisterNetworkCallback(networkCallback)
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
                //val items = carrelloViewModel.getItems()
                Snackbar.make(findViewById(android.R.id.content), "Aggiunto al carrello", Snackbar.LENGTH_SHORT).show()
            }


            private fun addToFavorites(item: Item?) {
                val authid = (auth.currentUser?.uid).toString()
                if (item?.nome != null) {
                val nuovoPreferito = hashMapOf(
                    "userId" to authid,
                    "nome" to item.nome,
                    "prezzo" to item.prezzo,
                    "foto" to item.foto
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
                }
            }

            private fun eliminaProdotto(nomeItem: String, tipo: String) {
                val collection = when (tipo) {
                    "pizza" -> db.collection("pizze")
                    "bibita" -> db.collection("bibite")
                    "dolce" -> db.collection("dolci")
                    else -> db.collection("offerte")
                }
                collection.whereEqualTo("nome", nomeItem)
                    .get()
                    .addOnSuccessListener { documents ->
                        for (document in documents) {
                            collection.document(document.id).delete()
                                .addOnSuccessListener {
                                    Log.d("DettagliProdottoActivity", "Prodotto eliminato!")
                                    val intent = Intent()
                                    setResult(RESULT_CODE_PRODOTTO_ELIMINATO, intent)
                                    finish()
                                }
                                .addOnFailureListener {
                                    Log.d("DettagliProdottoActivity", "Errore durante la rimozione")
                                }
                        }
                    }
                    .addOnFailureListener{ e ->
                        // Gestisci l'errore
                        Log.d("DettagliProdottoActivity", "Errore nella ricerca del documento")
                    }
            }



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



    override fun onProdottoModificato(item: Item){
        val textView : TextView = findViewById(R.id.textViewDettagli)
        val imageView : ImageView = findViewById(R.id.imageViewDettagli)
        val descrizione : TextView = findViewById(R.id.descrizioneDettagli)
        val prezzo : TextView = findViewById(R.id.prezzoProdotto)

        if(item != null){
            textView.text = item.nome
            descrizione.text = item.descrizione
            prezzo.text = item.prezzo.toString()

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

        val rootView = findViewById<View>(android.R.id.content)
        Snackbar.make(rootView, "Prodotto Modificato!", Snackbar.LENGTH_LONG).show()
    }

    override fun onBackPressed() {
        returnResult()
        // Chiama la tua funzione per restituire il risultato
        super.onBackPressed()
    }

    private fun returnResult() {
        val intent = Intent()
        intent.putParcelableArrayListExtra("itemsCarrello", ArrayList(carrelloViewModel.getItems()))
        setResult(RESULT_CODE_CARRELLO, intent)
    }
}








