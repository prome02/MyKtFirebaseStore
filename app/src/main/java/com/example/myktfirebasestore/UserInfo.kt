package com.example.myktfirebasestore

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

enum class Gender{
    M, F, L, G, T, Q, B, Other, Unknown
}

@Parcelize
data class UserInfo(
    var id: String = "", //id這個變數名稱不能隨便換，因為跟string resource的str_uid_tag是綁在一起的
    var name: String = "", var birthday: Date = Date(),
    var gender: Gender = Gender.Unknown, var language: String = "",
    var country: String = "", var desp: String = "",
    var register: Date = Date()
) : Parcelable

data class TravelInfo(var cityFrom:String, var cityTo:String, var willGo:Date, var willCome:Date, var info:String)
