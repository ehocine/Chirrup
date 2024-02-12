package com.hocel.chirrup.data.repository

import com.hocel.chirrup.data.api.ChirrupAPIService
import com.hocel.chirrup.data.models.chat.chatRequestBody.ChatRequestBody
import com.hocel.chirrup.data.models.chat.chat_response.ChatResponse
import com.hocel.chirrup.data.models.imageGeneration.apiRequest.RequestBody
import com.hocel.chirrup.data.models.imageGeneration.apiResponse.RequestResponse
import retrofit2.Response
import javax.inject.Inject

class ChirrupRepository @Inject constructor(private val chirrupAPIService: ChirrupAPIService) {

    suspend fun getChatResponse(
        chatRequestBody: ChatRequestBody
    ): Response<ChatResponse> {
        return chirrupAPIService.getChatResponse(
            chatRequestBody = chatRequestBody
        )
    }

    suspend fun getImageResponse(requestBody: RequestBody): Response<RequestResponse> =
        chirrupAPIService.getImageResponse(requestBody = requestBody)
}