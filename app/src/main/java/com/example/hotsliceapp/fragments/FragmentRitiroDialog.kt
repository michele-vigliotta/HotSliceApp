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
        fun onDialogPositiveClick(option: String, details: String)
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
                }
                R.id.radioServizioAsporto -> {
                    textView.text = "Inserisci l'orario di ritiro"
                    editText.hint = "HH:mm"
                    editText.visibility = View.VISIBLE
                    editText.isFocusable = false
                    editText.isFocusableInTouchMode = false

                    editText.setOnClickListener {
                        showTimePickerDialog()
                    }
                    editText.setText("")

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

    private fun showTimePickerDialog() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            requireContext(),
            R.style.CustomTimePickerDialog,
            { _, selectedHour, selectedMinute ->
                val selectedTime = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, selectedHour)
                    set(Calendar.MINUTE, selectedMinute)
                }
                val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                editText.setText(timeFormat.format(selectedTime.time))
            },
            hour,
            minute,
            true // true per formato 24 ore, false per formato 12 ore
        )
        timePickerDialog.show()
    }
}