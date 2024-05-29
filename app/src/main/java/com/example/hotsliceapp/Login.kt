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

class Login : AppCompatActivity() {

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
    }

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                     val intent = Intent(this, HomeActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    // Login riuscito, passa alla schermata principale(HomeActivity)
                    finish()
                } else {
                    // Login fallito, mostra un messaggio di errore
                    Toast.makeText(baseContext, "Autenticazione fallita.", Toast.LENGTH_SHORT)
                        .show()
                }
            }
    }

}
