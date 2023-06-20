package com.example.myhousev3.databases

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [OrderItem::class], version = 1)
abstract class OrderDb : RoomDatabase() {
    abstract fun getDao(): OrderDao

    companion object{
        fun getDb(context: Context): OrderDb {
            return Room.databaseBuilder(
                context.applicationContext,
                OrderDb::class.java,
                "order.db"
            ).build()
        }
    }
}