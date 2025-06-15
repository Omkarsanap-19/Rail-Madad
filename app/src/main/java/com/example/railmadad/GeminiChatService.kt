package com.example.railmadad



import android.util.Log
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.text.trimIndent
import kotlin.toString

class GeminiChatService {
    companion object {
        // Replace with your actual Gemini API key
        private const val API_KEY = "AIzaSyBDqkS0n_W-WH2xgmHsnGAnlI3Eon43ZN0"
        private const val MODEL_NAME = "gemini-1.5-flash"
    }

    private val generativeModel = GenerativeModel(
        modelName = MODEL_NAME,
        apiKey = API_KEY
    )

    private val chat = generativeModel.startChat(
        history = listOf(
            content(role = "user") { text("You are a helpful AI assistant for handling complaints and customer service. Please be polite, professional, and helpful. Always acknowledge the user's concern and try to gather necessary information to help resolve their issue.") },
            content(role = "model") { text("Hello! I'm your AI assistant, here to help you with any complaints or issues you may have. I'll do my best to provide instant acknowledgment and gather the necessary information to assist you. Please feel free to describe your concern, and I'll guide you through the process.") }
        )
    )

    suspend fun sendMessage(userMessage: String): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val response = chat.sendMessage(userMessage)
                val responseText = response.text ?: "I apologize, but I couldn't generate a proper response. Could you please try rephrasing your question?"
                Result.success(responseText)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun sendComplaintMessage(userMessage: String): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val enhancedPrompt = """
                    User's complaint/issue: $userMessage
                    
                    Please respond as a professional customer service AI assistant:
                    1. Acknowledge their concern empathetically
                    2. Ask relevant follow-up questions to gather more details
                    3. Provide helpful suggestions if applicable
                    4. Keep the response concise but thorough
                """.trimIndent()

                val response = chat.sendMessage(enhancedPrompt)
                val responseText = response.text ?: "I understand your concern. Let me help you with this issue. Could you please provide more details so I can assist you better?"
                Result.success(responseText)
            } catch (e: Exception) {
                Log.d("error geminiChatService",e.message.toString() )
                Result.failure(e)
            }
        }
    }
}