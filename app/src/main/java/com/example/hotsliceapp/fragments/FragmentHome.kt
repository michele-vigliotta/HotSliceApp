package com.example.hotsliceapp.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import com.example.hotsliceapp.AdapterListeHome
import com.example.hotsliceapp.Item
import com.example.hotsliceapp.R
import com.example.hotsliceapp.activities.Login
import com.example.hotsliceapp.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.Locale

class FragmentHome : Fragment() {
    private lateinit var auth: FirebaseAuth
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!! //getter per _binding (!! restituisce un non null)
    private var selectedButton: Button? = null
    private lateinit var buttonPizza: Button
    private lateinit var buttonBibite: Button
    private lateinit var buttonDolci: Button
    private lateinit var searchView: SearchView

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

        buttonPizza = binding.buttonPizza
        buttonBibite = binding.buttonBibite
        buttonDolci = binding.buttonDolci
        searchView = binding.searchView

        //Fragment di default
        if (savedInstanceState == null) {
            selectButton(buttonPizza)
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, FragmentPizza())
                .commit()
        }

        binding.buttonPizza.setOnClickListener {          //click listener per i pulsanti per cambaire fragment figlio da mostrare
            selectButton(buttonPizza)
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, FragmentPizza())
                .commit()
        }
        binding.buttonBibite.setOnClickListener {
            selectButton(buttonBibite)
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, FragmentBibite())
                .commit()
        }
        binding.buttonDolci.setOnClickListener {
            selectButton(buttonDolci)
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, FragmentDolci())
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
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    val fragmentPizza = parentFragmentManager.fragments.find { it is FragmentPizza } as? FragmentPizza
                    fragmentPizza?.filterList(it)

                    val fragmentDolci = parentFragmentManager.fragments.find { it is FragmentDolci } as? FragmentDolci
                    fragmentDolci?.filterList(it)

                    val fragmentBibite = parentFragmentManager.fragments.find { it is FragmentBibite } as? FragmentBibite
                    fragmentBibite?.filterList(it)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    val fragmentPizza = parentFragmentManager.fragments.find { it is FragmentPizza } as? FragmentPizza
                    fragmentPizza?.filterList(it)

                    val fragmentDolci = parentFragmentManager.fragments.find { it is FragmentDolci } as? FragmentDolci
                    fragmentDolci?.filterList(it)

                    val fragmentBibite = parentFragmentManager.fragments.find { it is FragmentBibite } as? FragmentBibite
                    fragmentBibite?.filterList(it)
                }
                return true
            }
        })

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun selectButton(button: Button) {
        selectedButton?.isSelected = false // Deseleziona il pulsante precedente
        button.isSelected = true // Seleziona il nuovo pulsante
        selectedButton = button // Memorizza il pulsante selezionato
    }
}