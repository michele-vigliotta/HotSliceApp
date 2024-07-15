package com.example.hotsliceapp

import android.widget.Toast
import org.junit.Assert
import org.junit.Test

//unit test per testare la funzione della search bar
class SearchBarUnitTest {

    @Test
    fun ricerca_Corretta() {

        var filteredList: List<Item> = listOf()

        val pizzaList: List<Item> = listOf(
            Item("Margherita"),
            Item("Marinara"),
            Item("Capricciosa"),
            Item("Diavola"))

        Assert.assertEquals(4, 2 + 2)
        fun filterList(query: String) {//metodo che filtra la lista quando si utilizza la searchview
            filteredList = pizzaList.filter { it.nome.contains(query, ignoreCase = true) }
        }

        filterList("Mar")
        Assert.assertEquals(2, filteredList.size)
        Assert.assertEquals("Margherita", filteredList[0].nome)
        Assert.assertEquals("Marinara", filteredList[1].nome)

        filterList("a")
        Assert.assertEquals(4, filteredList.size)

        filterList("")
        Assert.assertEquals(4, filteredList.size)
    }
}