package com.example.hotsliceapp.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hotsliceapp.AdapterListeHome
import com.example.hotsliceapp.Item
import com.example.hotsliceapp.ItemCarrello
import com.example.hotsliceapp.R
import com.example.hotsliceapp.activities.DettagliProdottoActivity
import com.example.hotsliceapp.activities.MainActivity
import com.google.firebase.firestore.FirebaseFirestore


class FragmentPreferiti:Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var preferitiAdapter: AdapterListeHome
    private val preferitiList = mutableListOf<Item>()
    private val REQUEST_CODE_DETTAGLI = 100
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_preferiti,container,false)
        progressBar = view.findViewById<ProgressBar>(R.id.progressBar)
        recyclerView = view.findViewById(R.id.recyclerPreferiti)
        recyclerView.layoutManager = LinearLayoutManager(context)

        preferitiAdapter = AdapterListeHome(preferitiList) //inizializza l'adapter con una lista vuota
        recyclerView.adapter = preferitiAdapter
        fetchDataFromFirebase()

        preferitiAdapter.onItemClick = {
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

        fetchDataFromFirebase()
    }

    private fun fetchDataFromFirebase() {
        preferitiList.clear()
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
                recyclerView.visibility = View.VISIBLE
                progressBar.visibility = View.GONE
            }
            .addOnFailureListener { exception ->
                Log.w("FragmentPreferiti", "Error getting documents.", exception)
            }
    }


}
