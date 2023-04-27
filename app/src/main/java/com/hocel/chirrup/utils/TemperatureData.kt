package com.hocel.chirrup.utils

enum class TemperatureData(val tempName: String, val tempValue: Float) {
    PRECISE(tempName = "Precise", tempValue = 0.2f),
    BALANCED(tempName = "Balanced", tempValue = 1f),
    CREATIVE(tempName = "Creative", tempValue = 1.8f)

}
