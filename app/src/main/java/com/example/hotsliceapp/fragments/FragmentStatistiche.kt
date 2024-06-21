package com.example.hotsliceapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
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
    private lateinit var radioGroupStatistiche: RadioGroup

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_statistiche, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewStatistiche)

        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = AdapterStatistiche()
        recyclerView.adapter = adapter

        radioGroupStatistiche = view.findViewById(R.id.radioGroupStatistiche)

        radioGroupStatistiche.setOnCheckedChangeListener { group, checkedId ->
            val timeInterval = when (checkedId) {
                R.id.radioWeek -> "week"
                R.id.radioMonth -> "month"
                R.id.radioYear -> "year"
                else -> ""
            }
            fetchOrders(timeInterval)
        }
        db = FirebaseFirestore.getInstance()

        return view
    }

    private fun fetchOrders(timeInterval: String) {
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val now = Calendar.getInstance()
        val startTime = Calendar.getInstance()

        when (timeInterval) {
            "week" -> startTime.add(Calendar.WEEK_OF_YEAR, -1)
            "month" -> startTime.add(Calendar.MONTH, -1)
            "year" -> startTime.add(Calendar.YEAR, -1)
            else -> {
                // Gestisci un valore non valido per timeInterval
                return
            }
        }

        db.collection("ordini")
            .whereGreaterThan("data", formatter.format(startTime.time))
            .orderBy("data", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                val productCount = mutableMapOf<String, Int>()

                for (document in documents) {
                    val descrizione = document.getString("descrizione")
                    descrizione?.split(";")?.forEach { item ->
                        val parts = item.split(",")
                        if (parts.size >= 2) {
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