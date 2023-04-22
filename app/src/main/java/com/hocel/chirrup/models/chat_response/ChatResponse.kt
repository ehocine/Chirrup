package com.hocel.chirrup.models.chat_response

data class ChatResponse(
    val choices: List<Choice>,
    val created: Int,
    val id: String,
    val model: String,
    val chatObject: String,
    val usage: Usage
)