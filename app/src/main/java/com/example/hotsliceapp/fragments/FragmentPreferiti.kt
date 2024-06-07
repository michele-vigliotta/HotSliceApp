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


class FragmentPreferiti:Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var preferitiAdapter: AdapterListeHome
    private val preferitiList = mutableListOf<Item>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_preferiti,container,false)

        recyclerView = view.findViewById(R.id.recyclerPreferiti)
        recyclerView.layoutManager = LinearLayoutManager(context)

        preferitiAdapter = AdapterListeHome(preferitiList) //inizializza l'adapter con una lista vuota
        recyclerView.adapter = preferitiAdapter
        fetchDataFromFirebase()

        preferitiAdapter.onItemClick = {
            val intent = Intent(activity, DettagliProdottoActivity::class.java)
            intent.putExtra("item", it)
            startActivity(intent)
        }

        return view




    }

    private fun fetchDataFromFirebase() {

        val db = FirebaseFirestore.getInstance()
        db.collection("preferiti")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    //converte ogni elemento in un oggetto e lo aggiunge alla lista
                    val preferito = document.toObject(Item::class.java)
                    preferitiList.add(preferito)
                }
                //aggiorna l'adapter con la nuova lista
                preferitiAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.w("FragmentPreferiti", "Error getting documents.", exception)
            }
    }


}
