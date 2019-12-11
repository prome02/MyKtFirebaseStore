package com.example.myktfirebasestore

import java.util.*

enum class Gender{
    M, F, L, G, T, Q, other
}
class UserInfo(var name:String,
               var bornYear: Date,
               var gen: Gender)

data class TravelInfo(var cityFrom:String, var cityTo:String, var willGo:Date, var willCome:Date, var info:String)
