package com.example.hotsliceapp

import android.os.Parcel
import android.os.Parcelable

data class Item(var nome : String = "",
                var foto : String? = null,
                var prezzo: Double = 0.0, //puó essere null
                var descrizione : String = ""
) : Parcelable {



    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readString(),
        parcel.readDouble()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(nome)
        parcel.writeString(foto)
        parcel.writeDouble(prezzo)

    }
    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Item>{
        override fun createFromParcel(parcel: Parcel): Item {
            return Item(parcel)
        }

        override fun newArray(size: Int): Array<Item?> {
            return arrayOfNulls(size)
        }
    }




}

