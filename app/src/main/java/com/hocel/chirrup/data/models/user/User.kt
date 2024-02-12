package com.hocel.chirrup.data.models.user

import com.hocel.chirrup.data.models.conversation.Conversation

data class User(
    var userID: String = "",
    var name: String = "",
    var email: String = "",
    var conversations: List<Conversation> = listOf(),
    var hasMessagesLimit: Boolean = true,
    var messagesLimit: Int = 10
)
