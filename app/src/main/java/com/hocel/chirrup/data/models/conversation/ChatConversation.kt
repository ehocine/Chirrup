package com.hocel.chirrup.data.models.conversation

import com.hocel.chirrup.data.models.chat.ChatMessage
import com.hocel.chirrup.data.models.chat.chatRequestBody.Message

data class ChatConversation(
    val listOfUserMessages: List<Message> = listOf(),
    val listOfMessagesConversation: List<ChatMessage> = listOf()
)
