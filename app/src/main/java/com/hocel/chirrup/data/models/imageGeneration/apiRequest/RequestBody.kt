package com.hocel.chirrup.data.models.imageGeneration.apiRequest

import com.hocel.chirrup.utils.ImageSize

data class RequestBody(
    val prompt: String = "",
    val n: Int = 1,
    val size: String = ImageSize.SMALL.size
)
