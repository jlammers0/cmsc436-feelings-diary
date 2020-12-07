package com.example.feelings_diary

import java.util.Calendar

data class PatientNotification (
    val year: Int = Calendar.YEAR,
    val month: Int = Calendar.MONTH,
    val day: Int = Calendar.DAY_OF_MONTH,
    val hour: Int = Calendar.HOUR_OF_DAY,
    val minute: Int = Calendar.MINUTE
)