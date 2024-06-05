package com.example.hotsliceapp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot

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
