import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.core.os.BundleCompat
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.example.hotsliceapp.Item
import com.example.hotsliceapp.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference


class FragmentModificaProdotto : DialogFragment() {
    private lateinit var storageRef: StorageReference
    var nomeFileFoto: String? = null
    val db = FirebaseFirestore.getInstance()
    lateinit var itemDaModificare: Item


    private var listener: ModificaProdottoListener? = null
    interface ModificaProdottoListener {
        fun onProdottoModificato(item: Item)
    }

    fun setModificaProdottoListener(listener: ModificaProdottoListener) {
        this.listener = listener
    }

    companion object{
        private const val PICK_IMAGE_REQUEST = 1
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        storageRef = FirebaseStorage.getInstance().reference

        val arguments = arguments ?: return Dialog(requireContext()) // Gestisci arguments nullo
        itemDaModificare = BundleCompat.getParcelable(arguments, "item", Item::class.java) ?: return Dialog(requireContext()) // Gestisci item nullo
        Log.d("FragmentModificaProdotto", "Item ricevuto: $itemDaModificare")
        val tipo = arguments.getString("prodotto")

        val titolo = "Modifica Prodotto"
        val builder = AlertDialog.Builder(requireContext())
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.fragment_modifica_prodotto, null)
        val fotoButton = view.findViewById<Button>(R.id.buttonUploadPhoto)
        val nomeTv = view.findViewById<TextView>(R.id.editTextName)
        val descrizioneEt = view.findViewById<EditText>(R.id.editTextDescription)
        val prezzoStrEt = view.findViewById<EditText>(R.id.editTextPrice)

        nomeTv.setText(itemDaModificare.nome)
        descrizioneEt.setText(itemDaModificare.descrizione)
        prezzoStrEt.setText(itemDaModificare.prezzo.toString())

        fotoButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        builder.setView(view)
            .setTitle(titolo)
            .setPositiveButton("Modifica", null)
            .setNegativeButton("Annulla"){ dialog, _ ->
                dialog.dismiss()
            }
        val dialog = builder.create()
        dialog.setOnShowListener {
            val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            positiveButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                .setTextColor(ContextCompat.getColor(requireContext(), R.color.red))

            positiveButton.setOnClickListener {
                val descrizione = descrizioneEt.text.toString()
                val prezzoStr = prezzoStrEt.text.toString()

                if ( prezzoStr.isEmpty() || descrizione.isEmpty()){
                    Toast.makeText(requireContext(), "Compilare tutti i campi", Toast.LENGTH_SHORT).show()
                }
                else{
                    val prezzo = prezzoStr.toDouble()
                    val nuovoProdotto = hashMapOf(
                        "prezzo" to prezzo,
                        "descrizione" to descrizione,
                    )

                    if (nomeFileFoto != null) {
                        nuovoProdotto["foto"] = nomeFileFoto!!
                    }

                    val collection = when (tipo) {
                        "pizza" -> db.collection("pizze")
                        "bibita" -> db.collection("bibite")
                        "dolce" -> db.collection("dolci")
                        else -> db.collection("offerte")
                    }

                    collection.whereEqualTo("nome", itemDaModificare.nome).get()
                        .addOnSuccessListener { documents ->
                            for (document in documents) {
                                val documentId = document.id
                                collection.document(documentId)
                                    .update(nuovoProdotto as Map<String, Any>)
                                    .addOnSuccessListener {
                                        itemDaModificare.descrizione = descrizione
                                        itemDaModificare.prezzo = prezzo
                                        if (nomeFileFoto != null) {
                                            itemDaModificare.foto = nomeFileFoto
                                        }
                                        listener?.onProdottoModificato(itemDaModificare)
                                        dismiss()
                                    }
                                    .addOnFailureListener { e ->
                                        Log.w("Firestore", "Errore durante l'aggiornamento del documento", e)
                                        Toast.makeText(requireActivity(), "Prodotto non modificato, riprova", Toast.LENGTH_SHORT).show()
                                    }
                            }
                        }
                        .addOnFailureListener { e ->
                            Log.w("Firestore", "Errore durante il recupero dei documenti", e)
                            Toast.makeText(requireActivity(), "Errore durante il recupero dei documenti, riprova", Toast.LENGTH_SHORT).show()
                        }
                }
            }
        }
        return dialog
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            val selectedImageUri = data.data
            nomeFileFoto = getFileName(selectedImageUri)
            uploadFoto(selectedImageUri, nomeFileFoto)
        }
    }

    private fun uploadFoto(uri: Uri?, nomeFileFoto: String?) {

        val imageRef = storageRef.child("$nomeFileFoto")
        val uploadTask = imageRef.putFile(uri!!)


        //Toast pericolosi, fanno crashare l'app
        /*
        uploadTask.addOnSuccessListener {
            Toast.makeText(requireContext(), "Immagine caricata con successo", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(requireContext(), "Errore durante il caricamento dell'immagine", Toast.LENGTH_SHORT).show()
        }
        */
    }

    private fun getFileName(uri: Uri?): String? {

        val cursor = requireContext().contentResolver.query(uri!!, null, null, null, null)
        cursor?.use {
            val nameIndex = it.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME)
            it.moveToFirst()
            return it.getString(nameIndex)
        }
        return "image_${System.currentTimeMillis()}.jpg"
    }

}