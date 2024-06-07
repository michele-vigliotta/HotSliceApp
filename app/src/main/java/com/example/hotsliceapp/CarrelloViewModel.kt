package com.example.hotsliceapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
class CarrelloViewModel : ViewModel() {
    private val _itemsCarrello = MutableLiveData<List<ItemCarrello>>(emptyList())
    val itemsCarrello: LiveData<List<ItemCarrello>> = _itemsCarrello


    fun addItem(item: ItemCarrello) {
        val currentItems = _itemsCarrello.value ?: emptyList()
        _itemsCarrello.value = currentItems + item
    }

    fun removeItem(item: ItemCarrello) {
        val currentItems = _itemsCarrello.value ?: emptyList()
        _itemsCarrello.value = currentItems - item
    }

    fun getItems(): MutableLiveData<List<ItemCarrello>> {
        return _itemsCarrello
    }

    fun clearItems() {
        _itemsCarrello.value = emptyList()
    }

}