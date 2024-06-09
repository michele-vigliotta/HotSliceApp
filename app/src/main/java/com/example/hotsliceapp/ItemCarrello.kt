package com.example.hotsliceapp

import android.os.Parcel
import android.os.Parcelable

data class ItemCarrello(
    var nome: String = "",
    var foto: String? = null,
    val prezzo: Double = 0.0,
    var quantita: Int = 0
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString(),
        parcel.readDouble(),
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(nome)
        parcel.writeString(foto)
        parcel.writeDouble(prezzo)
        parcel.writeInt(quantita)
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