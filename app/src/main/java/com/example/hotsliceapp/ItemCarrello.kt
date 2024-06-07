package com.example.hotsliceapp

data class ItemCarrello(var nome : String = "",
                        var foto : String? = null,
                        val prezzo: Double = 0.0, //puó essere null
                        var quantita : Int = 0
    ) {
}