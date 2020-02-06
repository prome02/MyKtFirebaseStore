package com.example.myktfirebasestore

import android.os.Parcelable
import android.util.Log
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
open class TravelData(
//variable name can not be changed,
// because it's related with name of the querying field in Firebase Firestore
    var cityFrom: String = "",
    var cityTo: String = "",
    var id: Long = -1,
    var transpotation: Int = -1,
    var transInfo: String = "",
    var dDepart: Date = Date(),
    var dArrive: Date = Date(),
    var description: String = "",
    var emailID: String = ""
) : Parcelable {
    constructor(dd: String, da: String, from: String, to: String, formattedStr: String) : this(
        from,
        to
    ) {

        setDateWithString(dd, da, formattedStr)
    }


    fun areContentsTheSame(da: TravelData): Boolean {

        if (dDepart.compareTo(da.dDepart) != 0) return false
        if (dArrive.compareTo(da.dArrive) != 0) return false
        if (cityFrom != da.cityFrom) return false
        if (cityTo != da.cityTo) return false
//         if(id!=da.id) return false
        return true
    }

    fun setDateWithString(dDStr: String, dAStr: String, formattedStr: String) {

        try {
            java.text.SimpleDateFormat(formattedStr).apply {
                dDepart = parse(dDStr)
                dArrive = parse(dAStr)
            }
        } catch (e: Exception) {
            Log.d("tda", e.message.toString())
        }
    }

    fun isIntersectedInDate(data: TravelData): Boolean {
        if (data.dDepart >= dArrive) return false
        else if (data.dArrive <= dDepart) return false
        else return true
    }

    fun makeContentTitle(): String {
        val dft = java.text.SimpleDateFormat("MMM dd")
        val strDp = dft.format(dDepart)
        val strAr = dft.format(dArrive)
        return "$strDp -> $strAr, from:$cityFrom   to:$cityTo"
    }

}