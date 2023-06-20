package com.example.myhousev3.chatmvvm

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [MessageItem::class], version = 1)
abstract class MessageDb : RoomDatabase() {
    abstract fun messageDao(): MessageDao

    companion object{
        fun getDb(context: Context): MessageDb {
            return Room.databaseBuilder(
                context.applicationContext,
                MessageDb::class.java,
                "messages.db"
            ).build()
        }
    }
}