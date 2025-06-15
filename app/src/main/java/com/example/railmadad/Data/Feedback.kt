package com.example.railmadad.Data

data class Feedback(
    var message: String? = "",
    var sentiment: List<SentimentResponse>? = emptyList()
)
