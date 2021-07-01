package com.iservice.iservice.classes

import android.os.Parcel
import android.os.Parcelable

class Servico(
    var uuid: String? = null,
    var servicoName: String? = null,
    var userUuid: String? = null,
    var tipo: String? = null,
    var descricao: String? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(uuid)
        parcel.writeString(servicoName)
        parcel.writeString(userUuid)
        parcel.writeString(tipo)
        parcel.writeString(descricao)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Servico> {
        override fun createFromParcel(parcel: Parcel): Servico {
            return Servico(parcel)
        }

        override fun newArray(size: Int): Array<Servico?> {
            return arrayOfNulls(size)
        }
    }
}