package com.hocel.chirrup.data.models.chat.chat_response

data class ChatResponse(
    val choices: List<Choice>,
    val created: Int,
    val id: String,
    val model: String,
    val chatObject: String,
    val usage: Usage
)