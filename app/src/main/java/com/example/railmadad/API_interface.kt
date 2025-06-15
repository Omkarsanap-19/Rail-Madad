package com.example.railmadad

import com.example.railmadad.Data.SentimentResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface API_interface {
    @POST("hf-inference/models/distilbert/distilbert-base-uncased-finetuned-sst-2-english")
    suspend fun textClassification(
        @Header("Authorization") authorization: String,
        @Header("Content-Type") contentType: String = "application/json",
        @Body request: TextClassificationRequest
    ): Response<List<List<TextClassificationResponse>>>
}

data class TextClassificationRequest(
    val inputs: String)

data class TextClassificationResponse(
    val label: String,
    val score: Double
)

data class TextClassificationResult(
    val success: Boolean,
    val classifications: List<SentimentResponse>,
    val error: String?
)
