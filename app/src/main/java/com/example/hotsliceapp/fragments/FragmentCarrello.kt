package com.example.hotsliceapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.hotsliceapp.R
import com.example.hotsliceapp.activities.MainActivity


class FragmentCarrello : Fragment() {



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_carrello, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mainActivity = activity as MainActivity
        val listaCarrello = mainActivity.listaCarrello
        val textView = view.findViewById<TextView>(R.id.textViewCarrello)
        if (listaCarrello.isEmpty()) {
            textView.text = "Il carrello è vuoto"
        }else{
            var stringa = ""
            var totale = 0.0
            for (item in listaCarrello) {
                stringa += "${item.quantita} - ${item.nome} - € ${item.prezzo} \n"
                totale += (item.quantita * item.prezzo)
            }
            stringa += "\nTotale: € ${totale}"
            textView.text = stringa
        }









    }
}