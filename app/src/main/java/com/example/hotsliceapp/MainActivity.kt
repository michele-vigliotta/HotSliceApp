package com.example.hotsliceapp

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.firestore

class MainActivity : AppCompatActivity() {

    val firestore = Firebase.firestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContentView(R.layout.activity_main)



        val intent = Intent(this, Login::class.java)

        (findViewById<Button>(R.id.button)).setOnClickListener {
            startActivity(intent)
        }

        val city = hashMapOf(
            "name" to "Los Angeles",
            "state" to "CA",
            "country" to "US",
        )

        firestore.collection("cities").document("LA")
            .set(city)
            .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
            .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }




        }
}

