package com.example.myktfirebasestore

import java.util.*
import kotlin.collections.ArrayList

data class UserContact(
    val id: String = "",
    val name: String = "",
    val alias: String = "",
    val roomValid: Boolean = false
)

data class RoomInfo(
    val name: String = "",
    val isValid: Boolean = false,
    var users: ArrayList<String> = ArrayList<String>()
)

data class ChattingMessage(val msg: String, val sender: Int, val time: Date)