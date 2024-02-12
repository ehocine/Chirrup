package com.hocel.chirrup.data.models.chat.chatRequestBody

data class ChatRequestBody(
    val messages: List<Message>,
    val model: String,
    val temperature: Float
)