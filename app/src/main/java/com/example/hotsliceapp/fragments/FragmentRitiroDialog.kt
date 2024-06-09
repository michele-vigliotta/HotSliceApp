import android.app.Dialog
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.example.hotsliceapp.R

class FragmentRitiroDialog : DialogFragment() {

    private var listener: RitiroDialogListener? = null

    interface RitiroDialogListener {
        fun onDialogPositiveClick(option: String, details: String)
    }

    fun setListener(listener: RitiroDialogListener) {
        this.listener = listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.fragment_ritiro_dialog, null)

        val radioGroup = view.findViewById<RadioGroup>(R.id.radioGroup)
        val radioServizioAlTavolo = view.findViewById<RadioButton>(R.id.radioServizioAlTavolo)
        val radioConsegnaADomicilio = view.findViewById<RadioButton>(R.id.radioConsegnaADomicilio)
        val textView = view.findViewById<TextView>(R.id.textViewDinamica)
        val editText = view.findViewById<EditText>(R.id.editTextDinamica)

        builder.setPositiveButton("OK", null)

        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radioServizioAlTavolo -> {
                    textView.text = "Inserisci il numero del tavolo"
                    editText.hint = "Numero del tavolo"
                    editText.inputType = InputType.TYPE_CLASS_NUMBER
                }
                R.id.radioConsegnaADomicilio -> {
                    textView.text = "Inserisci l'indirizzo di consegna"
                    editText.hint = "Indirizzo di consegna"
                    editText.inputType = InputType.TYPE_CLASS_TEXT
                }
            }

        }

        builder.setView(view)
            .setTitle("Scegli la modalità di ritiro")
            .setPositiveButton("OK",null)
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
                    R.id.radioConsegnaADomicilio -> "Consegna a Domicilio"
                    else -> ""
                }
                val enteredText = editText.text.toString()
                if (selectedOption.isEmpty() || enteredText.isEmpty()) {
                    Toast.makeText(requireContext(), "Compilare tutti i campi", Toast.LENGTH_SHORT).show()
                }else{
                    listener?.onDialogPositiveClick(selectedOption, enteredText)
                    dialog.dismiss()
                }
            }
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
        }
        return dialog
    }
}