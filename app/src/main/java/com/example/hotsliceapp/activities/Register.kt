package com.example.hotsliceapp.activities

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.hotsliceapp.R
import com.example.hotsliceapp.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth

class Register: AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding //Android studio genera una classe ActivityRegisterBinding a partire dal file activity_register.xml
    private lateinit var firebaseAuth: FirebaseAuth

    private var isInternetConnected: Boolean = false
    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var networkCallback: ConnectivityManager.NetworkCallback

    private lateinit var layoutRegister: View
    private lateinit var layoutNoInternet: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        layoutRegister = findViewById(R.id.layoutRegister)
        layoutNoInternet = findViewById(R.id.layoutNoInternet)

        firebaseAuth = FirebaseAuth.getInstance()

        connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        registerNetworkCallback() // Registra il NetworkCallback all'avvio
        checkInternetConnection() // Verifica la connessione iniziale
    }

    private fun checkInternetConnection(){
        val networkInfo = connectivityManager.activeNetworkInfo
        isInternetConnected = networkInfo != null && networkInfo.isConnected

        if(isInternetConnected){
            layoutRegister.visibility = View.VISIBLE
            layoutNoInternet.visibility = View.GONE
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
                            }
                            else if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                                Toast.makeText(this, "Formato Email non valido", Toast.LENGTH_SHORT).show()
                            }
                            else if(passw.length < 6){
                                Toast.makeText(this, "La password deve contenere almeno 6 caratteri", Toast.LENGTH_SHORT).show()
                            }
                            else {
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
        }else{
            layoutRegister.visibility = View.GONE
            layoutNoInternet.visibility = View.VISIBLE
        }
    }

    private fun registerNetworkCallback() {
        networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {

                runOnUiThread {
                    checkInternetConnection()
                }
            }

            override fun onLost(network: Network) {

                runOnUiThread {
                    checkInternetConnection()
                }
            }
        }
        connectivityManager.registerDefaultNetworkCallback(networkCallback)
    }

    override fun onDestroy() {
        super.onDestroy()
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }


}