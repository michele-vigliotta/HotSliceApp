package com.example.hotsliceapp

import android.media.Image
import android.os.Parcel
import android.os.Parcelable


data class Item(var image : Int, var nome : String = "", var prezzo: Double = 0.0) : Parcelable {

    constructor() : this(0, "", 0.0) // Costruttore senza argomenti
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readDouble()
    ) {
    }

    override fun describeContents(): Int {
        TODO("Not yet implemented")
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        TODO("Not yet implemented")
    }

    companion object CREATOR : Parcelable.Creator<Item> {
        override fun createFromParcel(parcel: Parcel): Item {
            return Item(parcel)
        }

        override fun newArray(size: Int): Array<Item?> {
            return arrayOfNulls(size)
        }
    }
}
