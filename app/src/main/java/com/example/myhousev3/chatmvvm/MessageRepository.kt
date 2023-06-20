package com.example.myhousev3.chatmvvm

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow

class MessageRepository(private val messageDao: MessageDao) {
    val allMessages: Flow<List<MessageItem>> = messageDao.getAllMessages()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(message: MessageItem) {
        messageDao.insertMessage(message)
    }
}