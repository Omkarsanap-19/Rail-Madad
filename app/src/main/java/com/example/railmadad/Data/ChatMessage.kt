package com.example.railmadad.Data

import java.util.UUID

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val message: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val isLoading: Boolean = false
) {
    companion object {
        fun createUserMessage(message: String): ChatMessage {
            return ChatMessage(
                message = message,
                isUser = true
            )
        }

        fun createBotMessage(message: String): ChatMessage {
            return ChatMessage(
                message = message,
                isUser = false
            )
        }

        fun createLoadingMessage(): ChatMessage {
            return ChatMessage(
                message = "",
                isUser = false,
                isLoading = true
            )
        }
    }
}
