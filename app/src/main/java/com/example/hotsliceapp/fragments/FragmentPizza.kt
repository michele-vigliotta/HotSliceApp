package com.example.hotsliceapp.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.google.firebase.firestore.FirebaseFirestore

class FragmentPizza:Fragment() {
    private lateinit var recyclerView: RecyclerView
    lateinit var pizzaAdapter: AdapterListeHome
    var pizzaList = mutableListOf<Item>()
    private val REQUEST_CODE_DETTAGLI = 100


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.pizza_fragment, container, false)

        recyclerView = view.findViewById(R.id.recyclerPizze)
        recyclerView.layoutManager = LinearLayoutManager(context)

        pizzaAdapter = AdapterListeHome(pizzaList) //inizializza l'adapter con una lista vuota
        recyclerView.adapter = pizzaAdapter
        fetchDataFromFirebase()

        pizzaAdapter.onItemClick = {
            val intent = Intent(activity, DettagliProdottoActivity::class.java)
            intent.putExtra("item", it)
            startActivityForResult(intent, REQUEST_CODE_DETTAGLI)
        }

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_DETTAGLI && resultCode == 200) {
            val itemsCarrello = data?.getParcelableArrayListExtra<ItemCarrello>("itemsCarrello")
            (activity as MainActivity).handleResult(itemsCarrello)
        }
    }

    private fun fetchDataFromFirebase() {

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
                pizzaAdapter.notifyDataSetChanged()
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
