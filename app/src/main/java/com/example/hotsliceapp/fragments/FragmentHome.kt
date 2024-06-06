package com.example.hotsliceapp.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.hotsliceapp.R
import com.example.hotsliceapp.activities.Login
import com.example.hotsliceapp.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class FragmentHome : Fragment() {
    private lateinit var auth: FirebaseAuth
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!! //getter per _binding (!! restituisce un non null)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false) //layout inflating
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth

        binding.buttonPizza.setOnClickListener {          //click listener per i pulsanti per cambaire fragment figlio da mostrare
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, FragmentPizza())
                .addToBackStack(null)
                .commit()
        }
        binding.buttonBibite.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, FragmentBibite())
                .addToBackStack(null)
                .commit()
        }
        binding.buttonDolci.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, FragmentDolci())
                .addToBackStack(null)
                .commit()
        }

        // codice per il logout
        val logout_btn = view.findViewById<ImageButton>(R.id.logout_button)
        logout_btn.setOnClickListener {
            auth.signOut()
            Toast.makeText(context, "Utente Disconnesso", Toast.LENGTH_SHORT).show()
            val intent = Intent(activity, Login::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}