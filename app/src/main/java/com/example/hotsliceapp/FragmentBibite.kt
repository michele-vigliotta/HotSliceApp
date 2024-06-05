package com.example.hotsliceapp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class FragmentBibite:Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var AdapterBibite: AdapterListeHome
    private val bibiteList = mutableListOf<Item>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.bibite_fragment,container,false)

        recyclerView = view.findViewById(R.id.recyclerBibite)
        recyclerView.layoutManager = LinearLayoutManager(context)

        AdapterBibite = AdapterListeHome(bibiteList)
        recyclerView.adapter = AdapterBibite
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
                AdapterBibite.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.w("PizzaFragment", "Error getting documents.", exception)
            }
    }
}