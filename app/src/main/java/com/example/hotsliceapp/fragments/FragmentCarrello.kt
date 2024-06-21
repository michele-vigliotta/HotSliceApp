package com.example.hotsliceapp.fragments

import AdapterCarrello
import FragmentRitiroDialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hotsliceapp.ItemCarrello
import com.example.hotsliceapp.R
import com.example.hotsliceapp.activities.MainActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Date


class FragmentCarrello : Fragment(), FragmentRitiroDialog.RitiroDialogListener {


    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AdapterCarrello
    private lateinit var auth: FirebaseAuth
    private lateinit var carrello: List<ItemCarrello>
    var string: String = ""
    val db = FirebaseFirestore.getInstance()
    private lateinit var totaleTextView: TextView
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_carrello, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressBar = view.findViewById<ProgressBar>(R.id.progressBar)

        recyclerView = view.findViewById(R.id.recyclerViewCarrello)
        recyclerView.layoutManager = LinearLayoutManager(context)

        val mainActivity = activity as MainActivity
        val listaCarrello = mainActivity.listaCarrello

        totaleTextView = view.findViewById<TextView>(R.id.textViewTotale)

        val updateTotal: (Double) -> Unit = { totale ->
            totaleTextView.text = "Totale: ${"%.2f".format(totale)} €"
        }
        adapter = AdapterCarrello(listaCarrello, updateTotal)
        recyclerView.adapter = adapter

        updateTotal(listaCarrello.sumByDouble { it.prezzo * it.quantita })

        //aggiorna la lista quando cambia
        adapter.getList() { newList ->
            (activity as? MainActivity)?.updateListaCarrello(newList)
            recyclerView.visibility = View.VISIBLE
            progressBar.visibility = View.GONE
        }

        val ordinaButton = view.findViewById<Button>(R.id.ordinaButton)


        //apre il dialog ritiro se il carrello non è vuoto
        ordinaButton.setOnClickListener {
            adapter.getList() { newList ->
                  carrello = newList
            }
            if (carrello.isEmpty()) {
                Toast.makeText(requireActivity(), "Carrello vuoto", Toast.LENGTH_SHORT).show()
            }
            else {
                val dialog = FragmentRitiroDialog()
                /*Imposta il listener. FragmentCarrello implementa FragmentRitiroDialog.RitiroDialogListener.
                Quando sul dialog viene premuto il pulsante Positive, onDialogPositiveClick viene chiamata */
                dialog.setListener(this)
                dialog.show(childFragmentManager, "RitiroDialog")
            }
        }

    }

    //metodo che gestisce il click positivo del dialog
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onDialogPositiveClick(option: String, details: String) {
        auth = Firebase.auth
        val authid = (auth.currentUser?.uid).toString()
        val ordiniCollection = db.collection("ordini")
        val oraRitiro = if (option == "Servizio d'Asporto") details else ""
        val tavolo = if (option == "Servizio al Tavolo") details else ""
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val data = LocalDateTime.now().format(formatter).toString()


        adapter.getList() { newList ->

            var totale: Double = 0.0
            for (item in newList) {
                string += "Nome: ${item.nome}, Quantità: ${item.quantita};\n"
                totale += (item.quantita * item.prezzo)
            }
            val nuovoOrdine = hashMapOf(
                "id" to "${authid}_${System.currentTimeMillis()}",
                "userId" to authid,
                "stato" to "in corso",
                "data" to data,
                "descrizione" to string,
                "tipo" to option,
                "ora" to oraRitiro,
                "tavolo" to tavolo,
                "totale" to totale.toString()
            )

            ordiniCollection.add(nuovoOrdine)
                .addOnSuccessListener { documentReference ->
                    Log.d("Firestore", "Documento aggiunto con ID: ${documentReference.id}")
                    Snackbar.make(requireView(), "Ordine effettuato", Snackbar.LENGTH_LONG).show()
                    // Imposta il totale a 0 € dopo aver effettuato l'ordine
                    totaleTextView.text = "Totale: 0.00 €"
                }
                .addOnFailureListener { e ->
                    Log.w("Firestore", "Errore durante l'aggiunta del documento", e)
                    Toast.makeText(requireActivity(), "Ordine non effettuato, riprova", Toast.LENGTH_SHORT).show()
                }
        }
        adapter.clearList()
    }
}