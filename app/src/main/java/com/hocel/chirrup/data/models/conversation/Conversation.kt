package com.hocel.chirrup.data.models.conversation

data class Conversation(
    val chatConversation: ChatConversation = ChatConversation(),
    val imageGenerationData: ImageGenerationData = ImageGenerationData()
)
