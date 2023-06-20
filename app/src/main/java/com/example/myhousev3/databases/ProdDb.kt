package com.example.myhousev3.databases

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ProdItem::class], version = 1)
abstract class ProdDb : RoomDatabase() {
    abstract fun getDao(): ProdDao

    companion object{
        fun getDb(context: Context): ProdDb {
            return Room.databaseBuilder(
                context.applicationContext,
                ProdDb::class.java,
                "products.db"
            ).build()
        }
    }
}