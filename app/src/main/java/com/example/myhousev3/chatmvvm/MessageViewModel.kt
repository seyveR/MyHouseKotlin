package com.example.myhousev3.chatmvvm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MessageViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: MessageRepository
    val allMessages: LiveData<List<MessageItem>>

    init {
        val messagesDao = MessageDb.getDb(application).messageDao()
        repository = MessageRepository(messagesDao)
        allMessages = repository.allMessages.asLiveData()
    }

    fun insert(message: MessageItem) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(message)
    }
}