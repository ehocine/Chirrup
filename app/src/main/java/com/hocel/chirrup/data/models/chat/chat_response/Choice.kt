package com.hocel.chirrup.data.models.chat.chat_response

import com.hocel.chirrup.data.models.chat.chat_response.Message

data class Choice(
    val finish_reason: String,
    val index: Int,
    val message: Message
)