package com.hocel.chirrup.data.repository

import com.hocel.chirrup.data.api.CompletionAPIService
import com.hocel.chirrup.models.chatRequestBody.ChatRequestBody
import com.hocel.chirrup.models.chat_response.ChatResponse
import retrofit2.Response
import javax.inject.Inject

class CompletionRepository @Inject constructor(private val completionAPIService: CompletionAPIService) {

    suspend fun getChatResponse(
        chatRequestBody: ChatRequestBody
    ): Response<ChatResponse> {
        return completionAPIService.getChatResponse(
            chatRequestBody = chatRequestBody
        )
    }
}