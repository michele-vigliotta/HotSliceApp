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

class FragmentDolci:Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var dolciAdapter: AdapterListeHome
    private val dolciList = mutableListOf<Item>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dolci_fragment, container, false)

        recyclerView = view.findViewById(R.id.recyclerDolci)
        recyclerView.layoutManager = LinearLayoutManager(context)

        dolciAdapter = AdapterListeHome(dolciList)
        recyclerView.adapter = dolciAdapter
        fetchDataFromFirebase()
        return view
    }

    private fun fetchDataFromFirebase() {

        val db = FirebaseFirestore.getInstance()
        db.collection("dolci")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val dolce = document.toObject(Item::class.java)
                    dolciList.add(dolce)
                }
                dolciAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.w("DolciFragment", "Error getting documents.", exception)
            }
    }

    fun filterList(query: String) {//metodo che filtra la lista quando si utilizza la searchview
        val filteredList = dolciList.filter { it.nome.contains(query, ignoreCase = true) }
        dolciAdapter.setFilteredList(filteredList)
        if (filteredList.isEmpty()) {
            // Se l'elenco filtrato è vuoto, mostra il messaggio
            Toast.makeText(context, "Dolce non presente nel menù", Toast.LENGTH_SHORT).show()
        }
    }
}
