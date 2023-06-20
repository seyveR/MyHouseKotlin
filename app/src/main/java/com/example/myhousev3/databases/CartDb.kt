package com.example.myhousev3.databases

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [CartItem::class], version = 1)
abstract class CartDb : RoomDatabase() {
    abstract fun getDao(): CartDao

    companion object{
        fun getDb(context: Context): CartDb {
            return Room.databaseBuilder(
                context.applicationContext,
                CartDb::class.java,
                "cart.db"
            ).build()
        }
    }
}