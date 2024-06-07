package com.example.hotsliceapp.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hotsliceapp.AdapterListeHome
import com.example.hotsliceapp.Item
import com.example.hotsliceapp.R
import com.example.hotsliceapp.activities.DettagliProdottoActivity
import com.google.firebase.firestore.FirebaseFirestore


class FragmentOfferte:Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var offerteAdapter: AdapterListeHome
    private val offerteList = mutableListOf<Item>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_offerte,container,false)

        recyclerView = view.findViewById(R.id.recyclerOfferte)
        recyclerView.layoutManager = LinearLayoutManager(context)

        offerteAdapter = AdapterListeHome(offerteList) //inizializza l'adapter con una lista vuota
        recyclerView.adapter = offerteAdapter
        fetchDataFromFirebase()

        offerteAdapter.onItemClick = {
            val intent = Intent(activity, DettagliProdottoActivity::class.java)
            intent.putExtra("item", it)
            startActivity(intent)
        }

        return view




    }

    private fun fetchDataFromFirebase() {

        val db = FirebaseFirestore.getInstance()
        db.collection("offerte")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    //converte ogni elemento in un oggetto e lo aggiunge alla lista
                    val offerta = document.toObject(Item::class.java)
                    offerteList.add(offerta)
                }
                //aggiorna l'adapter con la nuova lista
                offerteAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.w("offerteFragment", "Error getting documents.", exception)
            }
    }


}

