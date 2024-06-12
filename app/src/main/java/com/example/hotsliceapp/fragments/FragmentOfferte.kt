package com.example.hotsliceapp.fragments

import FragmentNuovoProdotto
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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


class FragmentOfferte:Fragment(), FragmentNuovoProdotto.NuovoProdottoListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var offerteAdapter: AdapterListeHome
    private val offerteList = mutableListOf<Item>()
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
        val view = inflater.inflate(R.layout.fragment_offerte,container,false)

        recyclerView = view.findViewById(R.id.recyclerOfferte)
        recyclerView.layoutManager = LinearLayoutManager(context)

        offerteAdapter = AdapterListeHome(offerteList) //inizializza l'adapter con una lista vuota
        recyclerView.adapter = offerteAdapter
        fetchDataFromFirebase()

        val floatingButton = view.findViewById<FloatingActionButton>(R.id.floatingActionButton)
        floatingButton.setOnClickListener {
            val prodotto = "Nuova Offerta"
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

        offerteAdapter.onItemClick = {
            val intent = Intent(activity, DettagliProdottoActivity::class.java)
            val prodotto = "offerta"
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
        offerteList.clear()

        val db = FirebaseFirestore.getInstance()
        db.collection("offerte")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    //converte ogni elemento in un oggetto e lo aggiunge alla lista
                    val offerta = document.toObject(Item::class.java)
                    offerteList.add(offerta)
                }
                //aggiorna l'adapter con la nuova lista
                offerteAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.w("offerteFragment", "Error getting documents.", exception)
            }
    }
}

