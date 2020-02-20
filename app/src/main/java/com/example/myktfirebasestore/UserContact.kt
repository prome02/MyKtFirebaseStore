package com.example.myktfirebasestore

import java.util.*

data class UserContact(
    val id: String = "",
    val name: String = "",
    val alias: String = "",
    val roomValid: Boolean = false
)

data class RoomInfo(
    val name: String = "",
    val isValid: Boolean = false,
    val user1: String = "",
    val user2: String = ""
)

data class ChattingMessage(val msg: String, val sender: Int, val time: Date)