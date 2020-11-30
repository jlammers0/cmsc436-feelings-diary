package com.example.feelings_diary

import java.util.*

data class DiaryEntry (
    val date: Date = Date(System.currentTimeMillis()),
    val feeling: Int = 0,
    val comment: String = "")