package com.example.myhousev3.chatmvvm

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {
    @Insert
    fun insertMessage(message: MessageItem)

    @Query("SELECT * FROM messages ORDER BY timestamp ASC")
    fun getAllMessages(): Flow<List<MessageItem>>

    @Query("SELECT * FROM messages WHERE user_email = :userEmail ORDER BY timestamp ASC")
    fun getMessagesByUserEmail(userEmail: String): Flow<List<MessageItem>>

    @Delete
    fun deleteMessage(message: MessageItem)
}