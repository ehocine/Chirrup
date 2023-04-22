package com.hocel.chirrup.models.user

import com.hocel.chirrup.models.conversation.Conversation

data class User(
    var userID: String = "",
    var name: String = "",
    var email: String = "",
    var conversations: List<Conversation> = listOf(),
    var hasMessagesLimit: Boolean = true,
    var messagesLimit: Int = 10
)
