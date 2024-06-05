package com.example.hotsliceapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.hotsliceapp.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth

class Register: AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding //Android studio genera una classe ActivityRegisterBinding a partire dal file activity_register.xml
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.btnRegister.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val passw = binding.etPassword.text.toString()

            if (email.isNotEmpty() && passw.isNotEmpty()) {
                firebaseAuth.createUserWithEmailAndPassword(email, passw)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(
                                this,
                                "Registrazione avvenuta con successo",
                                Toast.LENGTH_SHORT
                            ).show()
                            val intent = Intent(this, Login::class.java)
                            startActivity(intent)
                            finish() //chiude la vecchia activity appena siamo nella nuova
                        } else {
                            Toast.makeText(this, "Email già in uso", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Compilare tutti i campi", Toast.LENGTH_SHORT).show()
            }
        }
            binding.AlreadyRegisteredText.setOnClickListener{
                startActivity(Intent(this, Login::class.java))
                finish()
            }

    }
}