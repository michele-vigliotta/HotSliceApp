package com.example.hotsliceapp.activities
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkInfo
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.hotsliceapp.R
import com.example.hotsliceapp.databinding.ActivityLoginBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

class Login : AppCompatActivity() {

    private var isInternetConnected: Boolean = false
    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var networkCallback: ConnectivityManager.NetworkCallback

    private lateinit var binding: ActivityLoginBinding
    val db = Firebase.firestore
    private lateinit var auth: FirebaseAuth

    private lateinit var layoutLogin: View
    private lateinit var layoutNoInternet: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth //inizializza firebase auth
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        layoutLogin = findViewById(R.id.layoutLogin)
        layoutNoInternet = findViewById(R.id.layoutNoInternet)

        connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        registerNetworkCallback() // Registra il NetworkCallback all'avvio
        checkInternetConnection() // Verifica la connessione iniziale
    }

    private fun checkInternetConnection(){
        val networkInfo = connectivityManager.activeNetworkInfo
        isInternetConnected = networkInfo != null && networkInfo.isConnected

        if(isInternetConnected){
            layoutLogin.visibility = View.VISIBLE
            layoutNoInternet.visibility = View.GONE

            //SharedPreferences é una classe per memorizzare dati semplici che persistono anche quando l'app viene chiusa
            val sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE)
            val stayLoggedIn = sharedPreferences.getBoolean(
                "stayLoggedIn",
                false
            ) //mi da il valore booleano di stayLoggedIn, di default false
            if (stayLoggedIn && auth.currentUser != null) { //se l'utente è loggato e se stayLoggedIn è true
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }

            binding.btnLogin.setOnClickListener {
                val email = (findViewById<EditText>(R.id.etEmail)).text.toString()
                val passw = (findViewById<EditText>(R.id.etPassword)).text.toString()
                val rememberMe = binding.checkBoxStayLoggedIn.isChecked //stato checkbox

                if (email.isNotEmpty() && passw.isNotEmpty()) {
                    auth.signInWithEmailAndPassword(email, passw)
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                val editor =
                                    sharedPreferences.edit() //salva lo stato di rememberMe in staypLoggedIn
                                editor.putBoolean("stayLoggedIn", rememberMe)
                                editor.apply()

                                Toast.makeText(
                                    baseContext,
                                    "Login effettuato con successo",
                                    Toast.LENGTH_SHORT
                                ).show()
                                val intent = Intent(this, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            } else {
                                Toast.makeText(
                                    baseContext,
                                    "Email e/o password errati",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                } else {
                    Toast.makeText(baseContext, "Compilare tutti i campi", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            binding.NotRegisteredText.setOnClickListener {
                startActivity(Intent(this, Register::class.java))
                finish()
            }


        }else{
            layoutLogin.visibility = View.GONE
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
