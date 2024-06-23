import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.example.hotsliceapp.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class FragmentRitiroDialog : DialogFragment() {
    private var listener: RitiroDialogListener? = null
    private lateinit var editText: EditText

    //Interfaccia per comunicare con FragmentCarrello, onDialogPositiveClick dve essere implementata dal fragment chiamante
    interface RitiroDialogListener {
        fun onDialogPositiveClick(option: String, details: String, nome: String, numero: String)
    }
    //Metodo per settare il listener da FragmentCarrello
    fun setListener(listener: RitiroDialogListener) {
        this.listener = listener
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.fragment_ritiro_dialog, null)

        val radioGroup = view.findViewById<RadioGroup>(R.id.radioGroup)
        val radioServizioAlTavolo = view.findViewById<RadioButton>(R.id.radioServizioAlTavolo)
        val radioServizioAsporto = view.findViewById<RadioButton>(R.id.radioServizioAsporto)
        val textView = view.findViewById<TextView>(R.id.textViewDinamica)
        editText = view.findViewById<EditText>(R.id.editTextDinamica)
        val editTextNome = view.findViewById<EditText>(R.id.editTextNome)
        val editTextNumero = view.findViewById<EditText>(R.id.editTextTelefono)


        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radioServizioAlTavolo -> {
                    textView.text = "Inserisci il numero del tavolo"
                    editText.hint = "Numero del tavolo"
                    editText.inputType = InputType.TYPE_CLASS_NUMBER
                    editText.visibility = View.VISIBLE
                    editText.isFocusable = true
                    editText.isFocusableInTouchMode = true
                    
                    editText.setOnClickListener(null)
                    editText.setText("")
                    editTextNome.visibility = View.GONE
                    editTextNumero.visibility = View.GONE
                }
                R.id.radioServizioAsporto -> {
                    textView.text = "Inserisci l'orario di ritiro, il nome e il numero di telefono"
                    editText.hint = "HH:mm"
                    editText.visibility = View.VISIBLE
                    editText.isFocusable = false
                    editText.isFocusableInTouchMode = false

                    editText.setOnClickListener {
                        showTimePickerDialog()
                    }
                    editText.setText("")
                    editTextNome.visibility = View.VISIBLE
                    editTextNumero.visibility = View.VISIBLE
                    editTextNome.hint = "Nominativo"
                    editTextNumero.hint = "Numero di telefono"
                }
            }
        }

        builder.setView(view)
            .setTitle("Scegli la modalità di ritiro")
            .setPositiveButton("OK",null) //Imposta il listener a null
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
        val dialog = builder.create()
        dialog.setOnShowListener {
            val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            positiveButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
            positiveButton.setOnClickListener {
                val selectedOption = when(radioGroup.checkedRadioButtonId){
                    R.id.radioServizioAlTavolo -> "Servizio al Tavolo"
                    R.id.radioServizioAsporto -> "Servizio d'Asporto"
                    else -> ""
                }
                val enteredText = editText.text.toString()
                val nome = editTextNome.text.toString()
                val numero = editTextNumero.text.toString()



                if (selectedOption.isEmpty() || enteredText.isEmpty()) {
                    Toast.makeText(requireContext(), "Compilare tutti i campi", Toast.LENGTH_SHORT).show()
                }else if(selectedOption == "Servizio d'Asporto" && (nome.isEmpty() || numero.isEmpty() || numero.length != 10)){
                    Toast.makeText(requireContext(), "Compilare tutti i campi e inserire un numero di telefono valido", Toast.LENGTH_SHORT).show()
                }

                else{
                    listener?.onDialogPositiveClick(selectedOption, enteredText, nome, numero)
                    dialog.dismiss()
                }
            }
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
        }
        return dialog
    }

    private fun showTimePickerDialog() {
        val calendar = Calendar.getInstance()
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)
        val minHour = 19 // Ora minima consentita (19:00)
        val maxHour = 23 // Ora massima consentita (23:59)

        val timePickerDialog = TimePickerDialog(
            requireContext(),
            R.style.CustomTimePickerDialog,
            { _, selectedHour, selectedMinute ->
                if (selectedHour in minHour..maxHour &&
                (selectedHour > currentHour ||
                        (selectedHour == currentHour && selectedMinute >= currentMinute + 30))) {

                    val selectedTime = Calendar.getInstance().apply {
                        set(Calendar.HOUR_OF_DAY, selectedHour)
                        set(Calendar.MINUTE, selectedMinute)
                    }
                    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                    editText.setText(timeFormat.format(selectedTime.time))
                } else {
                    Toast.makeText(requireContext(), "Seleziona un orario valido tra le 19:00 e le 24:00, almeno 30 minuti dopo l'ora corrente.", Toast.LENGTH_LONG).show()
                }
            },
            currentHour,
            currentMinute,
            true // true per formato 24 ore, false per formato 12 ore
        )

        // Imposta i limiti per le ore selezionabili
        timePickerDialog.updateTime(currentHour, currentMinute + 30) // Imposta l'orario iniziale a 30 minuti dopo l'ora corrente

        timePickerDialog.show()
    }



}