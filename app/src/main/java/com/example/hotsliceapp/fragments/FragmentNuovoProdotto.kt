import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.example.hotsliceapp.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.time.LocalDate
import java.time.LocalTime

class FragmentNuovoProdotto : DialogFragment() {
    private lateinit var storageRef: StorageReference
    var nomeFileFoto: String? = null
    val db = FirebaseFirestore.getInstance()

    private var listener: NuovoProdottoListener? = null
    interface NuovoProdottoListener {
        fun onProdottoAggiunto()
    }

    fun setNuovoProdottoListener(listener: NuovoProdottoListener) {
        this.listener = listener
    }

    companion object{
        private const val PICK_IMAGE_REQUEST = 1
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        storageRef = FirebaseStorage.getInstance().reference
        val titolo = arguments?.getString("prodotto")
        val builder = AlertDialog.Builder(requireContext())
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.fragment_nuovo_prodotto, null)

        val fotoButton = view.findViewById<Button>(R.id.buttonUploadPhoto)

        fotoButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        builder.setView(view)
            .setTitle(titolo)
            .setPositiveButton("Aggiungi", null)
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
                val nome = view.findViewById<EditText>(R.id.editTextName).text.toString()
                val descrizione = view.findViewById<EditText>(R.id.editTextDescription).text.toString()
                val prezzoStr = view.findViewById<EditText>(R.id.editTextPrice).text.toString()

                if (nome.isEmpty() || prezzoStr.isEmpty() || descrizione.isEmpty()){
                    Toast.makeText(requireContext(), "Compilare tutti i campi", Toast.LENGTH_SHORT).show()
                }else if (nomeFileFoto == null){
                    Toast.makeText(requireContext(), "Inserire una foto e attendere", Toast.LENGTH_SHORT).show()
                }
                else{
                    val prezzo = prezzoStr.toDouble()
                    val nuovoProdotto = hashMapOf(
                        "nome" to nome,
                        "prezzo" to prezzo,
                        "descrizione" to descrizione,
                        "foto" to nomeFileFoto
                    )
                    val collection = if (titolo == "Nuova Pizza") {
                        db.collection("pizze")
                    } else {
                        db.collection("bevande")
                    }
                    collection.add(nuovoProdotto)
                        .addOnSuccessListener { documentReference ->
                            Log.d("Firestore", "Documento aggiunto con ID: ${documentReference.id}")
                            Toast.makeText(requireActivity(), "Prodotto aggiunto ", Toast.LENGTH_SHORT).show()
                            listener?.onProdottoAggiunto()
                            dismiss()
                        }
                        .addOnFailureListener { e ->
                            Log.w("Firestore", "Errore durante l'aggiunta del documento", e)
                            Toast.makeText(requireActivity(), "Prodotto non aggiunto, riprova", Toast.LENGTH_SHORT).show()
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