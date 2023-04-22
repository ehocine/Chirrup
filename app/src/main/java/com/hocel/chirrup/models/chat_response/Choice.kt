package com.hocel.chirrup.models.chat_response

import com.hocel.chirrup.models.chat_response.Message

data class Choice(
    val finish_reason: String,
    val index: Int,
    val message: Message
)