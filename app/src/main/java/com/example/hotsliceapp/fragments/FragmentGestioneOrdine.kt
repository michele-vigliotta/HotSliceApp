package com.example.hotsliceapp.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.example.hotsliceapp.ItemOrdine
import com.example.hotsliceapp.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class FragmentGestioneOrdine : DialogFragment() {
    private lateinit var editText: EditText

    companion object {
        private const val ARG_ORDINE = "ordine"

        fun newInstance(ordine: ItemOrdine): FragmentGestioneOrdine {
            val fragment = FragmentGestioneOrdine()
            val args = Bundle()
            args.putParcelable(ARG_ORDINE, ordine)
            fragment.arguments = args
            return fragment
        }
    }

    private var listener: GestioneOrdineListener? = null
    private var ordine: ItemOrdine? = null

    interface GestioneOrdineListener {
        fun onDialogPositiveClick(option: String, time: String, id: String)
    }

    fun setListener(listener: GestioneOrdineListener) {
        this.listener = listener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            ordine = it.getParcelable(ARG_ORDINE)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.fragment_gestione_ordini, null)

        val radioGroup = view.findViewById<RadioGroup>(R.id.radioGroup)
        val radioAccettato = view.findViewById<RadioButton>(R.id.radioAccettato)
        val radioRifiutato = view.findViewById<RadioButton>(R.id.radioRifiutato)
        val textView = view.findViewById<TextView>(R.id.textView)
        editText = view.findViewById<EditText>(R.id.editText)



        if (ordine?.tavolo == "") {
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

        builder.setView(view)
            .setTitle("Gestione ordine")
            .setPositiveButton("OK", null)
            .setNegativeButton("Annulla") { dialog, _ ->
                dialog.dismiss()
            }
        val dialog = builder.create()

        dialog.setOnShowListener {
            val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            positiveButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
            positiveButton.setOnClickListener {
                val selectedOption = when(radioGroup.checkedRadioButtonId) {
                    R.id.radioAccettato -> "Accettato"
                    R.id.radioRifiutato -> "Rifiutato"
                    else -> ""
                }
                if (selectedOption.isEmpty()) {
                    Toast.makeText(requireContext(), "Compilare tutti i campi", Toast.LENGTH_SHORT).show()
                }else if(ordine?.tavolo == "" && editText.text.toString().isEmpty()){
                    Toast.makeText(requireContext(), "Compilare tutti i campi", Toast.LENGTH_SHORT).show()
                }else{
                    listener?.onDialogPositiveClick(selectedOption, editText.text.toString(), ordine?.id.toString())
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