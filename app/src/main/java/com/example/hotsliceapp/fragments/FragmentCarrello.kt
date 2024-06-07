package com.example.hotsliceapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.example.hotsliceapp.CarrelloViewModel
import com.example.hotsliceapp.R


class FragmentCarrello : Fragment() {

    private lateinit var carrelloViewModel: CarrelloViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_carrello, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inizializza il ViewModel utilizzando ViewModelProvider
        carrelloViewModel = ViewModelProvider(requireActivity()).get(CarrelloViewModel::class.java)

        val textView = view.findViewById<TextView>(R.id.textViewCarrello)


        carrelloViewModel.itemsCarrello.observe(viewLifecycleOwner) { items ->
            val stringBuilder = StringBuilder()
            items.forEach { item ->
                stringBuilder.append("Item lesgoo: ${item.nome}, Quantity: ${item.quantita}\n")
            }
            textView.text = stringBuilder.toString()
        }

        val button = view.findViewById<TextView>(R.id.buttoncarrello)
        button.setOnClickListener {
            val items = carrelloViewModel.itemsCarrello.value ?: emptyList()
            Toast.makeText(context, items.toString(), Toast.LENGTH_SHORT).show()
        }
    }
}