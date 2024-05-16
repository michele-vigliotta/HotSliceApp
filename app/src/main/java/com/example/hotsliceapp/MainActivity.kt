package com.example.hotsliceapp

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

class MainActivity : AppCompatActivity() {

    val db = Firebase.firestore

    private lateinit var auth: FirebaseAuth

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContentView(R.layout.activity_main)

        auth = Firebase.auth

        val intent = Intent(this, Login::class.java)

        (findViewById<Button>(R.id.button)).setOnClickListener {
            startActivity(intent)
        }

        (findViewById<Button>(R.id.button2)).setOnClickListener {


            val authid = (auth.currentUser?.uid).toString()
            val documentSnapshot = db.collection("users").document(authid)
            documentSnapshot.get().addOnSuccessListener {
                document ->
                val role = document.getString("role")
                Toast.makeText(baseContext, "Role: $role", Toast.LENGTH_SHORT).show()
            }


        }

        (findViewById<Button>(R.id.button3)).setOnClickListener {

            auth.signOut()
            Toast.makeText(baseContext, "Utente Disconnesso", Toast.LENGTH_SHORT)
                .show()
        }







        }
}

