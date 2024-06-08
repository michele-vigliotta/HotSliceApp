package com.example.hotsliceapp.fragments

import AdapterCarrello
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hotsliceapp.R
import com.example.hotsliceapp.activities.MainActivity


class FragmentCarrello : Fragment() {


    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AdapterCarrello


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_carrello, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerViewCarrello)
        recyclerView.layoutManager = LinearLayoutManager(context)



        val mainActivity = activity as MainActivity
        val listaCarrello = mainActivity.listaCarrello


            adapter = AdapterCarrello(listaCarrello)
            recyclerView.adapter = adapter



        adapter.getList() { newList ->
            (activity as? MainActivity)?.updateListaCarrello(newList)
        }






    }
}