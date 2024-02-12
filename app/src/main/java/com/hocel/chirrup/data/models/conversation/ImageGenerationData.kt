package com.hocel.chirrup.data.models.conversation

import com.hocel.chirrup.data.models.imageGeneration.apiResponse.RequestResponse

data class ImageGenerationData(
    val prompt: String = "",
    val requestResponse: RequestResponse = RequestResponse()
)
