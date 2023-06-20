package com.example.myhousev3.databases

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [CatItem::class], version = 1)
abstract class CatDb : RoomDatabase() {
    abstract fun getDao(): CatDao

    companion object{
        fun getDb(context: Context): CatDb {
            return Room.databaseBuilder(
                context.applicationContext,
                CatDb::class.java,
                "categories.db"
            ).build()
        }
    }
}