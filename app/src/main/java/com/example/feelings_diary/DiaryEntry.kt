package com.example.feelings_diary

import java.util.*

data class DiaryEntry (
    val date: String = Date().toString(),
    val feeling: Int = 0,
    val long_date: Long = 0,
    val comment: String = "",
    val therapistComment: String = "")