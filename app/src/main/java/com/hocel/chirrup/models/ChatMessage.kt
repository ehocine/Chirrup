package com.hocel.chirrup.models

data class ChatMessage(
    var message: String = "",
    var date: Long = System.currentTimeMillis(),
    var messageFromBot: Boolean = false
)
