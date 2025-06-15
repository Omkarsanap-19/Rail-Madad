package com.example.railmadad.Data.cloud

data class Result(
    val access_mode: String,
    val asset_id: String,
    val bytes: Int,
    val created_at: String,
    val etag: String,
    val format: String,
    val height: Int,
    val public_id: String,
    val resource_type: String,
    val secure_url: String,
    val signature: String,
    val type: String,
    val url: String,
    val version: Int,
    val width: Int
)