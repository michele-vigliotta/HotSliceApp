package com.example.hotsliceapp.activities

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.hotsliceapp.ItemCarrello
import com.example.hotsliceapp.fragments.FragmentHome
import com.example.hotsliceapp.fragments.FragmentOfferte
import com.example.hotsliceapp.fragments.FragmentPreferiti
import com.example.hotsliceapp.R
import com.example.hotsliceapp.databinding.ActivityMainBinding
import com.example.hotsliceapp.fragments.FragmentCarrello
import com.example.hotsliceapp.fragments.FragmentOrdini
import com.example.hotsliceapp.fragments.FragmentStatistiche
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore


class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    val db = Firebase.firestore
    lateinit var role: String
    var listaCarrello: ArrayList<ItemCarrello> = arrayListOf()
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(FragmentHome()) //fragment che mostro di default
        val layout = binding.main
        val progressBar = binding.progressBar
        layout.visibility = View.GONE



        binding.bottomNavigationView.setOnItemSelectedListener {
            item -> val fragment:Fragment = when(item.itemId){
                R.id.bottom_offerte -> FragmentOfferte()
                R.id.bottom_preferiti -> FragmentPreferiti()
                R.id.bottom_home -> FragmentHome()
                R.id.bottom_carrello -> FragmentCarrello()
                R.id.bottom_ordini -> FragmentOrdini()
                R.id.bottom_statistiche -> FragmentStatistiche()
                else -> FragmentHome()
            }
            supportFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, fragment)
                .addToBackStack(null)
                .commit()
            true
        }
        val bottom_menu = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        //codice per mostrare il bottom menu in base al ruolo dell'utente
        auth = Firebase.auth
        val authid = (auth.currentUser?.uid).toString()
        val documentSnapshot = db.collection("users").document(authid)
        documentSnapshot.get().addOnSuccessListener {
                document ->
            role = document.getString("role").toString()
            if (role == "staff") {
                bottom_menu.menu.clear()
                bottom_menu.inflateMenu(R.menu.bottom_menu_staff)

            }
            else if (role == "admin"){
                bottom_menu.menu.clear()
                bottom_menu.inflateMenu(R.menu.bottom_menu_admin)
            }
            layout.visibility = View.VISIBLE
            progressBar.visibility = View.GONE
        }


    }

    fun handleResult(itemsCarrello: ArrayList<ItemCarrello>?) {
        // Gestisco il risultato preso dai fragment
        if (itemsCarrello != null) {
            listaCarrello.addAll(itemsCarrello)
            listaCarrello = mergeItemsWithSameName(listaCarrello)
        }
        Log.d("HandleResult", "Lista Carrello: $listaCarrello")
    }

    private fun mergeItemsWithSameName(items: ArrayList<ItemCarrello>): ArrayList<ItemCarrello> {
        val itemMap = mutableMapOf<String, ItemCarrello>()

        for (item in items) {
            if (itemMap.containsKey(item.nome)) {
                val existingItem = itemMap[item.nome]
                if (existingItem != null) {
                    existingItem.quantita += item.quantita
                }
            } else {
                itemMap[item.nome] = item
            }
        }
        return ArrayList(itemMap.values)
    }


    private fun replaceFragment(fragment: Fragment){

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout,fragment)
        fragmentTransaction.commit()
    }

    fun updateListaCarrello(newList: List<ItemCarrello>) {
        listaCarrello = newList as ArrayList<ItemCarrello>
    }

}
