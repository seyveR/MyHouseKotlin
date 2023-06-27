package com.example.myhousev3.chatmvvm

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "messages")
data class MessageItem (
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    @ColumnInfo(name = "user_email")
    var userEmail: String,
    @ColumnInfo(name = "message")
    var message: String,
    @ColumnInfo(name = "image_user")
    var imageUser: String,
    @ColumnInfo(name = "to_whom")
    var to_whom: String,
    @ColumnInfo(name = "timestamp")
    var timestamp: Long

)