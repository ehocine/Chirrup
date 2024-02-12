package com.hocel.chirrup.data.models.chat

data class ChatMessage(
    var message: String = "",
    var date: Long = System.currentTimeMillis(),
    var messageFromBot: Boolean = false
)
