package com.example.myhousev3

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object Migrations {
    val migration1to2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE `order` ADD COLUMN isProcessed INTEGER NOT NULL DEFAULT 0")
        }
    }
    val migration1to2Msg = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("CREATE TABLE IF NOT EXISTS `messages_new` " +
                    "(`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "`user_email` TEXT NOT NULL, " +
                    "`message` TEXT NOT NULL, " +
                    "`image_user` TEXT NOT NULL, " +
                    "`to_whom` TEXT NOT NULL DEFAULT '', " +
                    "`timestamp` INTEGER NOT NULL)")
            database.execSQL("INSERT INTO `messages_new` " +
                    "(`id`, `user_email`, `message`, `image_user`, `to_whom`, `timestamp`) " +
                    "SELECT `id`, `user_email`, `message`, `image_user`, `to_whom`, `timestamp` FROM `messages`")
            database.execSQL("DROP TABLE `messages`")
            database.execSQL("ALTER TABLE `messages_new` RENAME TO `messages`")
        }
    }
    val migration1to2User = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE `users` ADD COLUMN `address` TEXT")
        }
    }
}