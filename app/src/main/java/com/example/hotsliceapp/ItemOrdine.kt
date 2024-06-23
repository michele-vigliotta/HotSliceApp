package com.example.hotsliceapp

import android.os.Parcel
import android.os.Parcelable

data class ItemOrdine(
    val descrizione: String = "",
    var stato: String = "",
    val data: String = "",
    val id: String = "",
    var ora: String = "",
    val tavolo: String = "",
    val totale: String = "",
    val nome: String = "",
    val telefono: String = ""
) : Parcelable{

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(descrizione)
        parcel.writeString(stato)
        parcel.writeString(data)
        parcel.writeString(id)
        parcel.writeString(ora)
        parcel.writeString(tavolo)
        parcel.writeString(totale)
        parcel.writeString(nome)
        parcel.writeString(telefono)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ItemCarrello> {
        override fun createFromParcel(parcel: Parcel): ItemCarrello {
            return ItemCarrello(parcel)
        }

        override fun newArray(size: Int): Array<ItemCarrello?> {
            return arrayOfNulls(size)
        }
    }



}
