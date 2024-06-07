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
import com.example.hotsliceapp.R
import com.example.hotsliceapp.activities.DettagliProdottoActivity
import com.google.firebase.firestore.FirebaseFirestore

class FragmentDolci:Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var dolciAdapter: AdapterListeHome
    private val dolciList = mutableListOf<Item>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dolci_fragment,container,false)

        recyclerView = view.findViewById(R.id.recyclerDolci)
        recyclerView.layoutManager = LinearLayoutManager(context)

        dolciAdapter = AdapterListeHome(dolciList)
        recyclerView.adapter = dolciAdapter
        fetchDataFromFirebase()

        dolciAdapter.onItemClick = {
            val intent = Intent(activity, DettagliProdottoActivity::class.java)
            intent.putExtra("item", it)
            startActivity(intent)
        }

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
}
