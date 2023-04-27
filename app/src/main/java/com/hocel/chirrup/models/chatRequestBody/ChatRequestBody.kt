package com.hocel.chirrup.models.chatRequestBody

data class ChatRequestBody(
    val messages: List<Message>,
    val model: String,
    val temperature: Float
)