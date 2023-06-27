package com.example.myhousev3.databases

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity (tableName = "users")
data class UserItem (
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    @ColumnInfo(name = "firstname")
    var firstname: String,
    @ColumnInfo(name = "email")
    var email: String,
    @ColumnInfo(name = "pass")
    var pass: String,
    @ColumnInfo(name = "address")
    var address: String? = null,
    @ColumnInfo(name = "imageRes")
    var imageRes: String? = null,
    @ColumnInfo(name = "is_auth")
    var is_auth: Int = 0,
    @ColumnInfo(name = "is_admin")
    var is_admin: Int = 0,
)