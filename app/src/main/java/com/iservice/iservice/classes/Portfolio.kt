package com.iservice.iservice.classes

import android.os.Parcel
import android.os.Parcelable

class Portfolio (
    var uuid:String? = null,
    var userUuid:String? = null,
    val photoUrl: String? = null,
    var descricao: String? = null
        ): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(uuid)
        parcel.writeString(userUuid)
        parcel.writeString(photoUrl)
        parcel.writeString(descricao)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Portfolio> {
        override fun createFromParcel(parcel: Parcel): Portfolio {
            return Portfolio(parcel)
        }

        override fun newArray(size: Int): Array<Portfolio?> {
            return arrayOfNulls(size)
        }
    }
}