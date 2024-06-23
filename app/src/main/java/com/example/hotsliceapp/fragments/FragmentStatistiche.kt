package com.example.hotsliceapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.fragment.app.Fragment
import com.example.hotsliceapp.R
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class FragmentStatistiche : Fragment() {

    private lateinit var db: FirebaseFirestore
    private lateinit var radioGroupStatistiche: RadioGroup
    private lateinit var barChart: BarChart

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_statistiche, container, false)

        radioGroupStatistiche = view.findViewById(R.id.radioGroupStatistiche)
        barChart = view.findViewById(R.id.barChart)

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
                displayBarChart(sortedProducts)
            }
            .addOnFailureListener { exception ->
                // Gestisci l'errore
            }
    }

    private fun displayBarChart(data: Map<String, Int>) {
        val entries = ArrayList<BarEntry>()
        val labels = ArrayList<String>()
        var index = 0

        for ((key, value) in data) {
            entries.add(BarEntry(index.toFloat(), value.toFloat()))
            labels.add(key) // Aggiungi il nome del prodotto come etichetta
            index++
        }

        val dataSet = BarDataSet(entries, "Quantità vendite")
        val barData = BarData(dataSet)

        // Imposta le etichette sull'asse X
        barData.barWidth = 0.5f // Larghezza delle barre
        barChart.data = barData


        // Configura l'asse X
        val xAxis = barChart.xAxis
        val legend = barChart.legend
        xAxis.valueFormatter = IndexAxisValueFormatter(labels) // Imposta le etichette sull'asse X
        xAxis.position = XAxis.XAxisPosition.BOTTOM_INSIDE
        xAxis.setDrawGridLines(false) // Disabilita le linee di griglia verticali
        xAxis.granularity = 1f // Imposta la granularità dell'asse X per evitare sovrapposizioni

        barChart.description.isEnabled = false

        barChart.setVisibleXRangeMaximum(5f) // Imposta il numero massimo di valori visibili sull'asse X


        legend.setDrawInside(false) // Disabilita il disegno della legenda dentro il grafico
        legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        legend.yOffset = 0f

        barChart.invalidate() // Aggiorna il grafico
    }
}