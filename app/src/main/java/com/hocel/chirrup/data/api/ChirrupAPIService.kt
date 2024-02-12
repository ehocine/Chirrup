package com.hocel.chirrup.data.api


import com.hocel.chirrup.data.models.chat.chatRequestBody.ChatRequestBody
import com.hocel.chirrup.data.models.chat.chat_response.ChatResponse
import com.hocel.chirrup.data.models.imageGeneration.apiRequest.RequestBody
import com.hocel.chirrup.data.models.imageGeneration.apiResponse.RequestResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ChirrupAPIService {

    @POST("/v1/chat/completions")
    suspend fun getChatResponse(
        @Body chatRequestBody: ChatRequestBody
    ): Response<ChatResponse>

    @POST("/v1/images/generations")
    suspend fun getImageResponse(
        @Body requestBody: RequestBody
    ): Response<RequestResponse>
}