package com.example.myhousev3.databases

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.myhousev3.Migrations

@Database(entities = [OrderItem::class], version = 2)
abstract class OrderDb : RoomDatabase() {
    abstract fun getDao(): OrderDao

    companion object{
        fun getDb(context: Context): OrderDb {
            return Room.databaseBuilder(
                context.applicationContext,
                OrderDb::class.java,
                "order.db"
            ).addMigrations(Migrations.migration1to2)
                .build()
        }
    }
}