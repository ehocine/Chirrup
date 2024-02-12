package com.hocel.chirrup.data.models.imageGeneration.apiResponse

data class RequestResponse(
    val created: Long = System.currentTimeMillis(),
    val data: List<Data> = listOf()
)
