package com.example.myktfirebasestore

import java.util.*

enum class Gender{
    M, F, L, G, T, Q, other
}
class UserInfo(var name:String,
               var bornYear: Date,
               var gen: Gender)

//class TravelInfo(var city)