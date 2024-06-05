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

class FragmentPizza:Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var pizzaAdapter: AdapterListeHome
    private val pizzaList = mutableListOf<Item>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.pizza_fragment,container,false)

        recyclerView = view.findViewById(R.id.recyclerPizze)
        recyclerView.layoutManager = LinearLayoutManager(context)

        pizzaAdapter = AdapterListeHome(pizzaList)
        recyclerView.adapter = pizzaAdapter
        fetchDataFromFirebase()
        return view
    }

        private fun fetchDataFromFirebase() {

            val db = FirebaseFirestore.getInstance()
            db.collection("pizze")
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        val pizza = document.toObject(Item::class.java)
                        pizzaList.add(pizza)
                    }
                    pizzaAdapter.notifyDataSetChanged()
                }
                .addOnFailureListener { exception ->
                    Log.w("PizzaFragment", "Error getting documents.", exception)
                }
        }
    }
