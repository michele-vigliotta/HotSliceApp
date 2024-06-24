package com.example.hotsliceapp.fragments

import FragmentNuovoProdotto
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hotsliceapp.AdapterListeHome
import com.example.hotsliceapp.Item
import com.example.hotsliceapp.ItemCarrello
import com.example.hotsliceapp.R
import com.example.hotsliceapp.activities.DettagliProdottoActivity
import com.example.hotsliceapp.activities.MainActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class FragmentPizza:Fragment(), FragmentNuovoProdotto.NuovoProdottoListener {
    private lateinit var recyclerView: RecyclerView
    lateinit var pizzaAdapter: AdapterListeHome
    var pizzaList = mutableListOf<Item>()
    private val REQUEST_CODE_DETTAGLI = 100
    private val REQUEST_CODE_DETTAGLI_PRODOTTO = 200
    private lateinit var auth: FirebaseAuth
    private lateinit var db : FirebaseFirestore
    lateinit var role: String
    private lateinit var progressBar: ProgressBar

    override fun onProdottoAggiunto() {
        fetchDataFromFirebase()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.pizza_fragment, container, false)
        progressBar = view.findViewById<ProgressBar>(R.id.progressBar)
        recyclerView = view.findViewById(R.id.recyclerPizze)
        recyclerView.layoutManager = LinearLayoutManager(context)

        pizzaAdapter = AdapterListeHome(emptyList()) //inizializza l'adapter con una lista vuota
        recyclerView.adapter = pizzaAdapter

        db = Firebase.firestore
        auth = FirebaseAuth.getInstance()


        fetchDataFromFirebase()

        val floatingButton = view.findViewById<FloatingActionButton>(R.id.floatingActionButton)
        floatingButton.setOnClickListener {
            val prodotto = "Nuova Pizza"
            val dialog = FragmentNuovoProdotto()
            dialog.setNuovoProdottoListener(this)
            val bundle = Bundle()
            bundle.putString("prodotto", prodotto)
            dialog.arguments = bundle
            dialog.show(childFragmentManager, "NuovoProdotto")
        }
        auth = Firebase.auth
        val authid = (auth.currentUser?.uid).toString()
        val documentSnapshot = db.collection("users").document(authid)
        documentSnapshot.get().addOnSuccessListener {
                document ->
            role = document.getString("role").toString()
            if (role == "admin") {
                floatingButton.visibility = View.VISIBLE
            }
        }



        pizzaAdapter.onItemClick = {
            val intent = Intent(activity, DettagliProdottoActivity::class.java)
            val prodotto = "pizza"
            intent.putExtra("item", it)
            intent.putExtra("prodotto", prodotto)

            if (role == "admin") {
                startActivityForResult(intent, REQUEST_CODE_DETTAGLI_PRODOTTO)
            } else {
                startActivityForResult(intent, REQUEST_CODE_DETTAGLI)
            }
        }

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_DETTAGLI && resultCode == 200) {
            val itemsCarrello = data?.getParcelableArrayListExtra<ItemCarrello>("itemsCarrello")
            (activity as? MainActivity)?.handleResult(itemsCarrello)
        }
        if (resultCode == 201 && requestCode == REQUEST_CODE_DETTAGLI_PRODOTTO) {
                Snackbar.make(requireView(), "Prodotto eliminato", Snackbar.LENGTH_LONG).show()
        }
        fetchDataFromFirebase()
    }

    private fun fetchDataFromFirebase() {
        pizzaList.clear()

        val db = FirebaseFirestore.getInstance()
        db.collection("pizze")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    //converte ogni elemento in un oggetto e lo aggiunge alla lista
                    val pizza = document.toObject(Item::class.java)
                    pizzaList.add(pizza)
                }
                //aggiorna l'adapter con la nuova lista
                //pizzaAdapter.notifyDataSetChanged()
                pizzaAdapter.updateList(pizzaList)
                recyclerView.visibility = View.VISIBLE
                progressBar.visibility = View.GONE
            }
            .addOnFailureListener { exception ->
                Log.w("PizzaFragment", "Error getting documents.", exception)
            }
    }

    fun filterList(query: String) {//metodo che filtra la lista quando si utilizza la searchview
        val filteredList = pizzaList.filter { it.nome.contains(query, ignoreCase = true) }
        pizzaAdapter.setFilteredList(filteredList)
        if (filteredList.isEmpty()) {
            // Se l'elenco filtrato è vuoto, mostra il messaggio
            Toast.makeText(context, "Pizza non presente nel menù", Toast.LENGTH_SHORT).show()
        }
    }
}
