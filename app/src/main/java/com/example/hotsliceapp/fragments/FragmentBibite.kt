package com.example.hotsliceapp.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

class FragmentBibite:Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var AdapterBibite: AdapterListeHome
    private val bibiteList = mutableListOf<Item>()
    private val REQUEST_CODE_DETTAGLI = 100

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

        AdapterBibite.onItemClick = {
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
                Log.w("BibiteFragment", "Error getting documents.", exception)
            }
    }
}