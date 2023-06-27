package com.example.myhousev3.databases

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.myhousev3.Migrations.migration1to2User

@Database (entities = [UserItem::class], version = 2)
abstract class UserDb : RoomDatabase() {
    abstract fun getDao(): UserDao

    companion object{
        fun getDb(context: Context): UserDb {
            return Room.databaseBuilder(
                context.applicationContext,
                UserDb::class.java,
                "user.db"
            ).addMigrations(migration1to2User).build()
        }
    }
}