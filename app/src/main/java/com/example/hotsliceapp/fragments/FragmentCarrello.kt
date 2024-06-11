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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_carrello, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerViewCarrello)
        recyclerView.layoutManager = LinearLayoutManager(context)



        val mainActivity = activity as MainActivity
        val listaCarrello = mainActivity.listaCarrello


            adapter = AdapterCarrello(listaCarrello)
            recyclerView.adapter = adapter


        //aggiorna la lista quando cambia
        adapter.getList() { newList ->
            (activity as? MainActivity)?.updateListaCarrello(newList)
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
                dialog.show(parentFragmentManager, "RitiroDialog")
            }
        }

    }

    //metodo che gestisce il click positivo del dialog
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onDialogPositiveClick(option: String, details: String) {
        auth = Firebase.auth
        val authid = (auth.currentUser?.uid).toString()
        val ordiniCollection = db.collection("ordini")
        val indirizzo = if (option == "Consegna a Domicilio") details else ""
        val tavolo = if (option == "Servizio al Tavolo") details else ""
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val data = LocalDateTime.now().format(formatter).toString()


        adapter.getList() { newList ->


            for (item in newList) {
                string += "Nome: ${item.nome}, Quantità: ${item.quantita};\n"
            }
            val nuovoOrdine = hashMapOf(
                "id" to "${authid}_${System.currentTimeMillis()}",
                "userId" to authid,
                "stato" to "in corso",
                "data" to data,
                "descrizione" to string,
                "tipo" to option,
                "indirizzo" to indirizzo,
                "tavolo" to tavolo

            )

            ordiniCollection.add(nuovoOrdine)
                .addOnSuccessListener { documentReference ->
                    Log.d("Firestore", "Documento aggiunto con ID: ${documentReference.id}")
                    Snackbar.make(requireView(), "Ordine effettuato", Snackbar.LENGTH_LONG).show()                }
                .addOnFailureListener { e ->
                    Log.w("Firestore", "Errore durante l'aggiunta del documento", e)
                    Toast.makeText(requireActivity(), "Ordine non effettuato, riprova", Toast.LENGTH_SHORT).show()
                }
        }
        adapter.clearList()

    }
}