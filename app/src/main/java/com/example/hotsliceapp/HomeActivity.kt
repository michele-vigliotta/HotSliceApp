package com.example.hotsliceapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.hotsliceapp.databinding.ActivityHomeBinding
import com.example.hotsliceapp.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

class HomeActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    val db = Firebase.firestore
    lateinit var role: String
    lateinit var binding: ActivityHomeBinding                     //aggiunta
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityHomeBinding.inflate(layoutInflater)    //aggiunta
        setContentView(R.layout.activity_home)
        setContentView(binding.root)                           //aggiunta
        auth = Firebase.auth

        binding.buttonPizza.setOnClickListener {
            replaceFragment(FragmentPizza())
        }

        val authid = (auth.currentUser?.uid).toString()
        val documentSnapshot = db.collection("users").document(authid)
        documentSnapshot.get().addOnSuccessListener {
                document ->
             role = document.getString("role").toString()
            if (role == "staff") {
                var btnmenu = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
                btnmenu.menu.clear()
                btnmenu.inflateMenu(R.menu.bottom_menu_staff)

            }
            else if (role == "admin"){
                var btnmenu = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
                btnmenu.menu.clear()
                btnmenu.inflateMenu(R.menu.bottom_menu_admin)
            }

        }
    }

    private fun replaceFragment(fragment: Fragment) {

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainerView,fragment)
        fragmentTransaction.commit()
    }
}
