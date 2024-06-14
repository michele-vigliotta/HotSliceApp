import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hotsliceapp.ItemOrdine
import com.example.hotsliceapp.AdapterOrdini
import com.example.hotsliceapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class FragmentOrdini : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapterOrdini: AdapterOrdini
    private lateinit var ordiniList: MutableList<ItemOrdine>
    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()
    private var role: String = ""
    private var selectedButton: Button? = null
    private lateinit var buttonAlTavolo: Button
    private lateinit var buttonDAsporto: Button

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_ordini, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val linearLayoutButtons = view.findViewById<LinearLayout>(R.id.linearLayout)
        buttonAlTavolo = view.findViewById(R.id.buttonAlTavolo)
        buttonDAsporto = view.findViewById(R.id.buttonDAsporto)

        recyclerView = view.findViewById(R.id.recyclerViewOrdini)
        recyclerView.layoutManager = LinearLayoutManager(context)

        ordiniList = mutableListOf()
        adapterOrdini = AdapterOrdini(ordiniList)
        recyclerView.adapter = adapterOrdini

        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

        // Controllo se l'utente è loggato
        if (currentUser != null) {
            val authid = currentUser.uid
            val documentSnapshot = db.collection("users").document(authid)

            documentSnapshot.get().addOnSuccessListener { document ->
                role = document.getString("role").toString()

                // Mostra i pulsanti se l'utente è dello staff
                if (role == "staff") {
                    linearLayoutButtons.visibility = View.VISIBLE

                    // Imposta il pulsante "Al Tavolo" come selezionato di default
                    selectButton(buttonAlTavolo)
                    filterOrdini("Servizio al Tavolo")
                } else {
                    // Carica gli ordini per i clienti
                    loadOrdini(role, currentUser.uid)
                }

                // Configura i listener per i pulsanti
                buttonAlTavolo.setOnClickListener {
                    selectButton(buttonAlTavolo)
                    filterOrdini("Servizio al Tavolo")
                }

                buttonDAsporto.setOnClickListener {
                    selectButton(buttonDAsporto)
                    filterOrdini("Servizio d'Asporto")
                }
            }.addOnFailureListener { exception ->
                Log.w("OrdiniFragment", "Errore durante il recupero del documento utente", exception)
            }
        } else {
            Toast.makeText(context, "Devi essere loggato per visualizzare gli ordini", Toast.LENGTH_SHORT).show()
        }
    }

    private fun selectButton(button: Button) {
        selectedButton?.isSelected = false // Deseleziona il pulsante precedente
        button.isSelected = true // Seleziona il nuovo pulsante
        selectedButton = button // Memorizza il pulsante selezionato
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadOrdini(role: String, userId: String) {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val ordiniCollection = db.collection("ordini")

        // Determina la query di base in base al ruolo
        var query: Query = if (role == "staff") {
            ordiniCollection // Se è staff, carica tutti gli ordini
        } else {
            ordiniCollection.whereEqualTo("userId", userId) // Se è cliente, carica solo i suoi ordini
        }

        // Esegui la query di base e gestisci i risultati
        query.get().addOnSuccessListener { documents ->
            ordiniList.clear() // Pulisce la lista degli ordini
            for (document in documents) {
                val ordine = document.toObject(ItemOrdine::class.java)
                ordiniList.add(ordine)
            }

            // Ordina la lista per data decrescente
            ordiniList.sortByDescending { LocalDateTime.parse(it.data, formatter) }

            // Aggiorna la RecyclerView
            adapterOrdini.notifyDataSetChanged()
            recyclerView.scrollToPosition(0) // Scorre in cima alla lista
        }.addOnFailureListener { exception ->
            Log.w("OrdiniFragment", "Errore durante il recupero degli ordini", exception)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun filterOrdini(tipo: String) {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val ordiniCollection = db.collection("ordini")

        // Query base per lo staff
        var query: Query = ordiniCollection.whereEqualTo("tipo", tipo)

        // Esegui la query filtrata e gestisci i risultati
        query.get().addOnSuccessListener { documents ->
            ordiniList.clear() // Pulisce la lista degli ordini
            for (document in documents) {
                val ordine = document.toObject(ItemOrdine::class.java)
                ordiniList.add(ordine)
            }

            // Ordina la lista per data decrescente
            ordiniList.sortByDescending { LocalDateTime.parse(it.data, formatter) }

            // Aggiorna la RecyclerView
            adapterOrdini.notifyDataSetChanged()
            recyclerView.scrollToPosition(0) // Scorre in cima alla lista
        }.addOnFailureListener { exception ->
            Log.w("OrdiniFragment", "Errore durante il recupero degli ordini filtrati", exception)
        }
    }
}
