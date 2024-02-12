package com.hocel.chirrup.data.models.chat.chat_response

data class Usage(
    val completion_tokens: Int,
    val prompt_tokens: Int,
    val total_tokens: Int
)