package com.example.hotsliceapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

class Login : AppCompatActivity() {
    val db = Firebase.firestore
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = Firebase.auth //inizializza firebase auth

        val btnlogin : Button = findViewById(R.id.btnLogin)
        btnlogin.setOnClickListener {
            val email = (findViewById<EditText>(R.id.etEmail)).text.toString()
            val passw = (findViewById<EditText>(R.id.etPassword)).text.toString()
            loginUser(email, passw)
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

        //codice logout
        (findViewById<Button>(R.id.button3)).setOnClickListener {
            auth.signOut()
            Toast.makeText(baseContext, "Utente Disconnesso", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) { //login ok
                     val intent = Intent(this, HomeActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK //elimina le altre activity
                    startActivity(intent)
                    finish()
                } else {
                    // Login fallito, mostra un messaggio di errore
                    Toast.makeText(baseContext, "Autenticazione fallita. Assicurati di avere un account valido", Toast.LENGTH_SHORT)
                        .show()
                }
            }
    }

}
