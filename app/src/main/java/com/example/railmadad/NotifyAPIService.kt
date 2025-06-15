package com.example.railmadad

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface NotifyAPIService {
    @Headers("Content-Type: application/json")
    @POST("sendNotification")
    fun sendNotification(@Body request: NotificationRequest): Call<Map<String, Any>>
}

data class NotificationRequest(
    val token: String,
    val title: String,
    val message: String
)
