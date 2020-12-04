package com.example.feelings_diary

import java.util.*

data class Message(
    val date: Date = Date(),
    val from: String = "",
    val to: String = "",
    val messageType: MessageType = MessageType.MESSAGE,
    val subject: String = "",
    val body: String =""

)