package com.iservice.iservice.classes

import android.os.Parcel
import android.os.Parcelable

class Users(
    val uuid: String? = null, val username: String? = null, val profileUrl: String? = null,
    val cpf:String? = null, val dataNasc:String? = null, val accountType: String? = null,
    val cep: String? = null, val logradouro: String? = null, val bairro:String? = null,
    val cidade: String? = null, val estado: String? = null, val totalAvalAtend:Double = 0.0,
    val totalAvalServ:Double = 0.0, val totalAvalOrc:Double = 0.0, val numAval:Int = 0,
    val token:String? = null, val online:Boolean? = null) :Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readInt() == 1
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(uuid)
        parcel.writeString(username)
        parcel.writeString(profileUrl)
        parcel.writeString(cpf)
        parcel.writeString(dataNasc)
        parcel.writeString(accountType)
        parcel.writeString(cep)
        parcel.writeString(logradouro)
        parcel.writeString(bairro)
        parcel.writeString(cidade)
        parcel.writeString(estado)
        parcel.writeDouble(totalAvalAtend)
        parcel.writeDouble(totalAvalServ)
        parcel.writeDouble(totalAvalOrc)
        parcel.writeInt(numAval)
        parcel.writeString(token)
        parcel.writeInt(if (online!!) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Users> {
        override fun createFromParcel(parcel: Parcel): Users {
            return Users(parcel)
        }

        override fun newArray(size: Int): Array<Users?> {
            return arrayOfNulls(size)
        }
    }
}