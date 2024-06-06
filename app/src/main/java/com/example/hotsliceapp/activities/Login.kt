package com.example.hotsliceapp.activities
import android.content.Intent
import android.os.Bundle
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
    private lateinit var binding: ActivityLoginBinding
    val db = Firebase.firestore
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth //inizializza firebase auth


        //SharedPreferences é una classe per memorizzare dati semplici che persistono anche quando l'app viene chiusa
        val sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE)
        val stayLoggedIn = sharedPreferences.getBoolean("stayLoggedIn", false) //mi da il valore booleano di stayLoggedIn, di default false
        if (stayLoggedIn && auth.currentUser != null) { //se l'utente è loggato e se stayLoggedIn è true
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }

        binding.btnLogin.setOnClickListener {
            val email = (findViewById<EditText>(R.id.etEmail)).text.toString()
            val passw = (findViewById<EditText>(R.id.etPassword)).text.toString()
            val rememberMe = binding.checkBoxStayLoggedIn.isChecked //stato checkbox

            if (email.isNotEmpty() && passw.isNotEmpty()){
                auth.signInWithEmailAndPassword(email, passw)
                    .addOnCompleteListener(this) {task->
                        if(task.isSuccessful){

                            if(email == "mainactivity@gmail.com"){val intent = Intent(this, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            }else {


                                val editor =
                                    sharedPreferences.edit() //salva lo stato di rememberMe in staypLoggedIn
                                editor.putBoolean("stayLoggedIn", rememberMe)
                                editor.apply()

                                Toast.makeText(
                                    baseContext,
                                    "Login effettuato con successo",
                                    Toast.LENGTH_SHORT
                                ).show()
                                val intent = Intent(this, HomeActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                        }

                        else{
                            Toast.makeText(baseContext, "Email e/o password errati", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
            else{
                Toast.makeText(baseContext, "Compilare tutti i campi", Toast.LENGTH_SHORT).show()
            }
        }

        binding.NotRegisteredText.setOnClickListener {
            startActivity(Intent(this, Register::class.java))
            finish()
        }


        /*
        //codice per controllare il ruolo
        binding.button2.setOnClickListener {
            val authid = (auth.currentUser?.uid).toString()
            val documentSnapshot = db.collection("users").document(authid)
            documentSnapshot.get().addOnSuccessListener {
                    document ->
                val role = document.getString("role")
                Toast.makeText(baseContext, "Role: $role", Toast.LENGTH_SHORT).show()
            }
        } */

    }
}
