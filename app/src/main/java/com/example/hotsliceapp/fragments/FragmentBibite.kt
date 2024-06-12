package com.example.hotsliceapp.fragments

import FragmentNuovoProdotto
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hotsliceapp.AdapterListeHome
import com.example.hotsliceapp.Item
import com.example.hotsliceapp.ItemCarrello
import com.example.hotsliceapp.R
import com.example.hotsliceapp.activities.DettagliProdottoActivity
import com.example.hotsliceapp.activities.MainActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class FragmentBibite:Fragment(), FragmentNuovoProdotto.NuovoProdottoListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var bibiteAdapter: AdapterListeHome
    private val bibiteList = mutableListOf<Item>()
    private val REQUEST_CODE_DETTAGLI = 100
    private val REQUEST_CODE_DETTAGLI_PRODOTTO = 200
    private lateinit var auth: FirebaseAuth
    val db = Firebase.firestore
    lateinit var role: String


    override fun onProdottoAggiunto() {
        fetchDataFromFirebase()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.bibite_fragment, container, false)

        recyclerView = view.findViewById(R.id.recyclerBibite)
        recyclerView.layoutManager = LinearLayoutManager(context)

        bibiteAdapter = AdapterListeHome(bibiteList)
        recyclerView.adapter = bibiteAdapter
        fetchDataFromFirebase()

        val floatingButton = view.findViewById<FloatingActionButton>(R.id.floatingActionButton)
        floatingButton.setOnClickListener {
            val prodotto = "Nuova Bibita"
            val dialog = FragmentNuovoProdotto()
            dialog.setNuovoProdottoListener(this)
            val bundle = Bundle()
            bundle.putString("prodotto", prodotto)
            dialog.arguments = bundle
            dialog.show(childFragmentManager, "NuovoProdotto")
        }
        auth = Firebase.auth
        val authid = (auth.currentUser?.uid).toString()
        val documentSnapshot = db.collection("users").document(authid)
        documentSnapshot.get().addOnSuccessListener {
                document ->
            role = document.getString("role").toString()
            if (role == "staff") {
                floatingButton.visibility = View.VISIBLE
            }
        }

        bibiteAdapter.onItemClick = {
            val intent = Intent(activity, DettagliProdottoActivity::class.java)
            val prodotto = "bibita"
            intent.putExtra("item", it)
            intent.putExtra("prodotto", prodotto)

            if (role == "staff") {
                startActivityForResult(intent, REQUEST_CODE_DETTAGLI_PRODOTTO)
            } else {
                startActivityForResult(intent, REQUEST_CODE_DETTAGLI)
            }
        }

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_DETTAGLI && resultCode == 200) {
            val itemsCarrello = data?.getParcelableArrayListExtra<ItemCarrello>("itemsCarrello")
            (activity as MainActivity).handleResult(itemsCarrello)
        }
        if (resultCode == 201 && requestCode == REQUEST_CODE_DETTAGLI_PRODOTTO) {
            Snackbar.make(requireView(), "Prodotto eliminato", Snackbar.LENGTH_LONG).show()
        }
        fetchDataFromFirebase()
    }

    private fun fetchDataFromFirebase() {
        bibiteList.clear()
        val db = FirebaseFirestore.getInstance()
        db.collection("bibite")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val bibita = document.toObject(Item::class.java)
                    bibiteList.add(bibita)
                }
                bibiteAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.w("BibiteFragment", "Error getting documents.", exception)
            }
    }

    fun filterList(query: String) {//metodo che filtra la lista quando si utilizza la searchview
        val filteredList = bibiteList.filter { it.nome.contains(query, ignoreCase = true) }
        bibiteAdapter.setFilteredList(filteredList)
        if (filteredList.isEmpty()) {
            // Se l'elenco filtrato è vuoto, mostra il messaggio
            Toast.makeText(context, "Bibita non presente nel menù", Toast.LENGTH_SHORT).show()
        }
    }
}
