package com.example.hotsliceapp.fragments

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
import com.example.hotsliceapp.R
import com.google.firebase.firestore.FirebaseFirestore

class FragmentBibite:Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var bibiteAdapter: AdapterListeHome
    private val bibiteList = mutableListOf<Item>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.bibite_fragment, container, false)

        recyclerView = view.findViewById(R.id.recyclerBibite)
        recyclerView.layoutManager = LinearLayoutManager(context)

        bibiteAdapter = AdapterListeHome(bibiteList)
        recyclerView.adapter = bibiteAdapter
        fetchDataFromFirebase()
        return view
    }

    private fun fetchDataFromFirebase() {

        val db = FirebaseFirestore.getInstance()
        db.collection("bibite")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val bibita = document.toObject(Item::class.java)
                    bibiteList.add(bibita)
                }
                bibiteAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.w("BibiteFragment", "Error getting documents.", exception)
            }
    }

    fun filterList(query: String) {//metodo che filtra la lista quando si utilizza la searchview
        val filteredList = bibiteList.filter { it.nome.contains(query, ignoreCase = true) }
        bibiteAdapter.setFilteredList(filteredList)
        if (filteredList.isEmpty()) {
            // Se l'elenco filtrato è vuoto, mostra il messaggio
            Toast.makeText(context, "Bibita non presente nel menù", Toast.LENGTH_SHORT).show()
        }
    }
}