package com.hocel.chirrup.data.api


import com.hocel.chirrup.models.chatRequestBody.ChatRequestBody
import com.hocel.chirrup.models.chat_response.ChatResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface CompletionAPIService {

    @POST("/v1/chat/completions")
    suspend fun getChatResponse(
        @Body chatRequestBody: ChatRequestBody
    ): Response<ChatResponse>
}