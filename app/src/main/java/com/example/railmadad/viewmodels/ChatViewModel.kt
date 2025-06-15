package com.example.railmadad.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.railmadad.Data.ChatMessage
import com.example.railmadad.GeminiChatService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.collections.removeLastOrNull
import kotlin.collections.toList
import kotlin.fold
import kotlin.text.isEmpty
import kotlin.text.trim

class ChatViewModel : ViewModel() {
    private val chatService = GeminiChatService()

    private val _messages = MutableLiveData<List<ChatMessage>>()
    val messages: LiveData<List<ChatMessage>> = _messages

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val messageList = mutableListOf<ChatMessage>()

    init {
        // Add welcome message
        val welcomeMessage = ChatMessage.createBotMessage(
            "Hello! I'm your AI assistant here to help with complaints and customer service. How can I assist you today?"
        )
        messageList.add(welcomeMessage)
        _messages.value = messageList.toList()
    }

    fun sendMessage(userMessage: String) {
        if (userMessage.trim().isEmpty()) return

        // Add user message
        val userChatMessage = ChatMessage.createUserMessage(userMessage.trim())
        messageList.add(userChatMessage)

        // Add loading message
        val loadingMessage = ChatMessage.createLoadingMessage()
        messageList.add(loadingMessage)
        _messages.value = messageList.toList()

        _isLoading.value = true
        _error.value = null

        // ithe viewmodelScope use karne instead of CoroutineScope(Dispatchers.Main)
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val result = chatService.sendComplaintMessage(userMessage.trim())

                // Remove loading message
                messageList.removeLastOrNull()

                result.fold(
                    onSuccess = { response ->
                        val botMessage = ChatMessage.createBotMessage(response)
                        messageList.add(botMessage)
                        _messages.value = messageList.toList()
                    },
                    onFailure = { exception ->
                        val errorMessage = ChatMessage.createBotMessage(
                            "I apologize, but I'm having trouble connecting right now. Please try again in a moment."
                        )
                        messageList.add(errorMessage)
                        _messages.value = messageList.toList()
                        _error.value = exception.message
                        Log.d("error ChatViewModel", "Error sending message: ${exception.message}")
                    }
                )
            } catch (e: Exception) {
                // Remove loading message
                messageList.removeLastOrNull()

                val errorMessage = ChatMessage.createBotMessage(
                    "I'm sorry, something went wrong. Please try again."
                )
                messageList.add(errorMessage)
                _messages.value = messageList.toList()
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}