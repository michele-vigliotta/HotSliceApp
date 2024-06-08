package com.example.hotsliceapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
class CarrelloViewModel : ViewModel() {
    private var itemsCarrello: MutableList<ItemCarrello> = mutableListOf()

    fun addItem(item: ItemCarrello) {
        itemsCarrello.add(item)
    }

    fun removeItem(item: ItemCarrello) {
        itemsCarrello.remove(item)
    }

    fun getItems(): List<ItemCarrello> {
        return itemsCarrello.toList()
    }

    fun setItems(newItems: ArrayList<ItemCarrello>?) {
        itemsCarrello = newItems?.toMutableList() ?: mutableListOf()
    }
    fun clearItems() {
        itemsCarrello.clear()
    }
}