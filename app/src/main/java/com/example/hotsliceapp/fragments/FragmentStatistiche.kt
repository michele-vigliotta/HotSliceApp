package com.example.hotsliceapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hotsliceapp.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class FragmentStatistiche : Fragment() {

    private lateinit var db: FirebaseFirestore
    private lateinit var adapter: AdapterStatistiche

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_statistiche, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewStatistiche)

        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = AdapterStatistiche()
        recyclerView.adapter = adapter

        db = FirebaseFirestore.getInstance()
        fetchOrders()

        return view
    }

    private fun fetchOrders() {
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val now = Calendar.getInstance()
        val lastMonth = Calendar.getInstance()
        lastMonth.add(Calendar.MONTH, -1)

        db.collection("ordini")
            .whereGreaterThan("data", formatter.format(lastMonth.time))
            .orderBy("data", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                val productCount = mutableMapOf<String, Int>()

                for (document in documents) {
                    val descrizione = document.getString("descrizione")
                    descrizione?.split(";")?.forEach { item ->
                        val parts = item.split(",")
                        if (parts.size >= 2) { // Verifica che ci siano almeno 2 parti
                            val nome = parts[0].split(":").getOrNull(1)?.trim()
                            val quantitaStr = parts[1].split(":").getOrNull(1)?.trim()

                            if (nome != null && quantitaStr != null) {
                                try {
                                    val quantita = quantitaStr.toInt()
                                    productCount[nome] = productCount.getOrDefault(nome, 0) + quantita
                                } catch (e: NumberFormatException) {
                                    // Gestisci l'errore di conversione da stringa a intero
                                }
                            }
                        }
                    }
                }

                val sortedProducts = productCount.toList().sortedByDescending { (_, value) -> value }.toMap()
                adapter.setData(sortedProducts)
            }
            .addOnFailureListener { exception ->
                // Gestisci l'errore
            }
    }
}
