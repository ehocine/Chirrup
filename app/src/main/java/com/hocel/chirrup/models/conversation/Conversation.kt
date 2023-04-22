package com.hocel.chirrup.models.conversation

import com.hocel.chirrup.models.ChatMessage
import com.hocel.chirrup.models.chatRequestBody.Message

data class Conversation(
    val listOfUserMessages: List<Message> = listOf(),
    val listOfMessagesConversation: List<ChatMessage> = listOf()
)
