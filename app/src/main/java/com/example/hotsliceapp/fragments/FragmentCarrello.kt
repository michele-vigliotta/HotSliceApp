package com.example.hotsliceapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.hotsliceapp.CarrelloViewModel
import com.example.hotsliceapp.CarrelloViewModelFactory
import com.example.hotsliceapp.R


class FragmentCarrello : Fragment() {

    private lateinit var CarrelloViewModel : CarrelloViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_carrello, container, false)

        CarrelloViewModel = ViewModelProvider(requireActivity()).get(CarrelloViewModel::class.java)





    }


}