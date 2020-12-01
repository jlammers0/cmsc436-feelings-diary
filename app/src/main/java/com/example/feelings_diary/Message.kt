package com.example.feelings_diary

import java.util.*

data class Message(
    val date: Date = Date(System.currentTimeMillis()),
    val from: User = User(),
    val to: User = User(),
    val messageType: MessageType = MessageType.MESSAGE,
    val subject: String = "",
    val body: String =""

)