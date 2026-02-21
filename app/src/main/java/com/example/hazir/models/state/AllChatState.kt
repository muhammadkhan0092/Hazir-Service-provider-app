package com.example.hazir.models.state

import com.example.hazir.models.MessageModel

data class AllChatState(
    val isLoading  : Boolean = true,
    val messages : List<MessageModel> = emptyList()
)
