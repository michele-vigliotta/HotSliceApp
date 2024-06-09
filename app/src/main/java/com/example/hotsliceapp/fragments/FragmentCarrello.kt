package com.example.hotsliceapp.fragments

import AdapterCarrello
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
import com.example.hotsliceapp.R
import com.example.hotsliceapp.activities.MainActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDate
import java.time.LocalTime


class FragmentCarrello : Fragment() {


    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AdapterCarrello
    private lateinit var auth: FirebaseAuth

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



        adapter.getList() { newList ->
            (activity as? MainActivity)?.updateListaCarrello(newList)
        }

        val db = FirebaseFirestore.getInstance()
        auth = Firebase.auth
        val authid = (auth.currentUser?.uid).toString()
        val ordiniCollection = db.collection("ordini")

        val ordinaButton = view.findViewById<Button>(R.id.ordinaButton)
        var string: String = ""
        ordinaButton.setOnClickListener {
            adapter.getList() { newList ->
                for (item in newList) {
                     string += "Nome: ${item.nome}, Quantità: ${item.quantita};\n"
                }
                val nuovoOrdine = hashMapOf(
                    "id" to "${authid}_${System.currentTimeMillis()}",
                    "userId" to authid,
                    "stato" to "in corso",
                    "data" to "${LocalDate.now()}  ${LocalTime.now()}",
                    "descrizione" to string
                )
                ordiniCollection.add(nuovoOrdine)
                    .addOnSuccessListener { documentReference ->
                        Log.d("Firestore", "Documento aggiunto con ID: ${documentReference.id}")
                        Toast.makeText(requireActivity(), "Ordine effettuato", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Log.w("Firestore", "Errore durante l'aggiunta del documento", e)
                        Toast.makeText(requireActivity(), "Ordine non effettuato, riprova", Toast.LENGTH_SHORT).show()
                    }
            }
            adapter.clearList()
        }





    }
}