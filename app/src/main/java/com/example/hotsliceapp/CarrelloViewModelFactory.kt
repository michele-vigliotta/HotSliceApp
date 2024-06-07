package com.example.hotsliceapp
import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

//Questa factory garantisce che lo stesso CarrelloViewModel viene usato in tutta l'app
class CarrelloViewModelFactory(private val application: Application)
    : ViewModelProvider.AndroidViewModelFactory(application) {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CarrelloViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return CarrelloViewModel() as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
}