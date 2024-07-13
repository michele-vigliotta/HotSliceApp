import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.example.hotsliceapp.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.io.InputStream

class FragmentNuovoProdotto : DialogFragment() {
    private lateinit var storageRef: StorageReference
    private var nomeFileFoto: String? = null
    private val db = FirebaseFirestore.getInstance()
    private var listener: NuovoProdottoListener? = null
    private lateinit var progressBar: ProgressBar
    private var isImageUploaded = false
    private lateinit var imagePreview: ImageView
    private var networkReceiver: BroadcastReceiver? = null
    private var isInternetConnected: Boolean = true
    private var uploadTask: UploadTask? = null

    interface NuovoProdottoListener {
        fun onProdottoAggiunto()
    }

    fun setNuovoProdottoListener(listener: NuovoProdottoListener) {
        this.listener = listener
    }

    companion object {
        private const val PICK_IMAGE_REQUEST = 1
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        storageRef = FirebaseStorage.getInstance().reference
        val titolo = arguments?.getString("prodotto")
        val builder = AlertDialog.Builder(requireContext())
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.fragment_nuovo_prodotto, null)


        // Trova il ProgressBar, ImageView e altri elementi della vista
        progressBar = view.findViewById(R.id.progressBar)
        imagePreview = view.findViewById(R.id.imagePreview)
        val fotoButton = view.findViewById<Button>(R.id.buttonUploadPhoto)

        fotoButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        builder.setView(view)
            .setTitle(titolo)
            .setPositiveButton("Aggiungi", null)
            .setNegativeButton("Annulla") { dialog, _ ->
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

                if (nome.isEmpty() || prezzoStr.isEmpty() || descrizione.isEmpty()) {
                    Toast.makeText(requireContext(), "Compilare tutti i campi", Toast.LENGTH_SHORT).show()
                } else if (nomeFileFoto == null || !isImageUploaded) {
                    Toast.makeText(requireContext(), "Inserire una foto e attendere", Toast.LENGTH_SHORT).show()
                } else {
                    val prezzo = prezzoStr.toDouble()
                    val collection = when (titolo) {
                        "Nuova Pizza" -> db.collection("pizze")
                        "Nuova Bibita" -> db.collection("bibite")
                        "Nuovo Dolce" -> db.collection("dolci")
                        else -> db.collection("offerte")
                    }

                    // Verifica se il prodotto esiste già
                    collection.whereEqualTo("nome", nome).get()
                        .addOnSuccessListener { querySnapshot ->
                            if (!querySnapshot.isEmpty) {
                                // Nome prodotto già esistente
                                Toast.makeText(requireContext(), "Prodotto già presente nel menù", Toast.LENGTH_SHORT).show()
                            } else {
                                // Nome prodotto non esiste, procedi con l'aggiunta
                                val nuovoProdotto = hashMapOf(
                                    "nome" to nome,
                                    "prezzo" to prezzo,
                                    "descrizione" to descrizione,
                                    "foto" to nomeFileFoto
                                )
                                collection.add(nuovoProdotto)
                                    .addOnSuccessListener { documentReference ->
                                        Log.d("Firestore", "Documento aggiunto con ID: ${documentReference.id}")
                                        Toast.makeText(requireActivity(), "Prodotto aggiunto", Toast.LENGTH_SHORT).show()
                                        listener?.onProdottoAggiunto()
                                        dismiss()
                                    }
                                    .addOnFailureListener { e ->
                                        Log.w("Firestore", "Errore durante l'aggiunta del documento", e)
                                        Toast.makeText(requireActivity(), "Prodotto non aggiunto, riprova", Toast.LENGTH_SHORT).show()
                                    }
                            }
                        }
                        .addOnFailureListener { e ->
                            Log.w("Firestore", "Errore durante la verifica del prodotto", e)
                            Toast.makeText(requireContext(), "Errore durante la verifica del prodotto", Toast.LENGTH_SHORT).show()
                        }
                }
            }
        }

        return dialog
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        uploadTask?.let {
            it.cancel()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            val selectedImageUri = data.data
            nomeFileFoto = getFileName(selectedImageUri)
            showImagePreview(selectedImageUri)
            uploadFoto(selectedImageUri, nomeFileFoto)
        }
    }

    private fun showImagePreview(uri: Uri?) {
        uri?.let {
            val inputStream: InputStream? = requireContext().contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            imagePreview.setImageBitmap(bitmap)
            imagePreview.visibility = View.VISIBLE
        }
    }

    private fun uploadFoto(uri: Uri?, nomeFileFoto: String?) {
        val imageRef = storageRef.child("$nomeFileFoto")
        uploadTask = imageRef.putFile(uri!!)

        // Mostra la ProgressBar
        progressBar.visibility = View.VISIBLE
        isImageUploaded = false  // Assicurati che il flag sia impostato su false prima di iniziare

        uploadTask?.addOnSuccessListener {
            // Nascondi la ProgressBar e aggiorna il flag
            progressBar.visibility = View.GONE
            isImageUploaded = true
            Toast.makeText(requireContext(), "Immagine caricata con successo", Toast.LENGTH_SHORT).show()
        }?.addOnFailureListener {
            // Nascondi la ProgressBar e aggiorna il flag
            progressBar.visibility = View.GONE
            isImageUploaded = false
            Toast.makeText(requireContext(), "Errore durante il caricamento dell'immagine", Toast.LENGTH_SHORT).show()
        }
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

    private fun registerNetworkReceiver() {
        val intentFilter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        networkReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val connectivityManager = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
                isInternetConnected = activeNetwork?.isConnectedOrConnecting == true

                if (!isInternetConnected) {
                    Toast.makeText(context, "Connessione Internet persa", Toast.LENGTH_SHORT).show()
                    dismiss()
                }
            }
        }
        context?.registerReceiver(networkReceiver, intentFilter)
    }

    private fun unregisterNetworkReceiver() {
        networkReceiver?.let {
            context?.unregisterReceiver(it)
        }
    }
}