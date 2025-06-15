package com.example.railmadad.Data

import com.google.firebase.database.Exclude

data class FeedGet(
    var message: String? = "",
    var sentiment: List<SentimentResponse>? = emptyList(),
    @get:Exclude var sign: String? = ""
)
