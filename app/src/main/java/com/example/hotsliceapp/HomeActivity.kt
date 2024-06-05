package com.example.hotsliceapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.hotsliceapp.databinding.ActivityHomeBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

class HomeActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    val db = Firebase.firestore
    lateinit var role: String
    lateinit var binding: ActivityHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding=ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Aggiunge il FragmentPizza di default se savedInstanceState è null
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, FragmentPizza())
                .commit()
        }

        binding.buttonPizza.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, FragmentPizza())
                .commit()        }
        binding.buttonBibite.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, FragmentBibite())
                .commit()        }
        binding.buttonDolci.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, FragmentDolci())
                .commit()        }

        //codice per mostrare il bottom menu in base al ruolo dell'utente
        auth = Firebase.auth
        val authid = (auth.currentUser?.uid).toString()
        val documentSnapshot = db.collection("users").document(authid)
        documentSnapshot.get().addOnSuccessListener {
                document ->
             role = document.getString("role").toString()
            if (role == "staff") {
                var bottom_menu = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
                bottom_menu.menu.clear()
                bottom_menu.inflateMenu(R.menu.bottom_menu_staff)

            }
            else if (role == "admin"){
                var btnmenu = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
                btnmenu.menu.clear()
                btnmenu.inflateMenu(R.menu.bottom_menu_admin)
            }
        }

        //codice per il logout
        var logout_btn= findViewById<Button>(R.id.logout_button)
        logout_btn.setOnClickListener {
            auth.signOut()
            Toast.makeText(baseContext, "Utente Disconnesso", Toast.LENGTH_SHORT)
                .show()
            val intent = Intent(this, Login::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }


}
