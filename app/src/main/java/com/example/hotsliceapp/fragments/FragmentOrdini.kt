package com.example.hotsliceapp.fragments

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hotsliceapp.ItemOrdine
import com.example.hotsliceapp.AdapterOrdini
import com.example.hotsliceapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FragmentOrdini.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentOrdini : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapterOrdini: AdapterOrdini
    private lateinit var ordiniList: MutableList<ItemOrdine>
    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_ordini, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FragmentOrdini.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FragmentOrdini().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerViewOrdini)
        recyclerView.layoutManager = LinearLayoutManager(context)

        ordiniList = mutableListOf()
        adapterOrdini = AdapterOrdini(ordiniList)
        recyclerView.adapter = adapterOrdini

        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

        if (currentUser != null) {
            db.collection("ordini")
                .whereEqualTo("userId", currentUser.uid)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val ordine = document.toObject(ItemOrdine::class.java)
                        ordiniList.add(ordine)
                    }
                    ordiniList.sortByDescending { LocalDateTime.parse(it.data, formatter) }

                    adapterOrdini.notifyDataSetChanged()
                }
                .addOnFailureListener { exception ->
                    // Gestisci l'errore
                }
        }
    }
}
