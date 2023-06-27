package com.example.myhousev3.databases

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

@Dao
interface UserDao {
    @Insert
    fun insertUser(user: UserItem)

    @Query("UPDATE users SET address = :address WHERE is_auth = 1")
    fun updateCurrentUserAddress(address: String)


    @Query("SELECT * FROM users")
    fun getAllUser(): Flow<List<UserItem>>

    @Query("SELECT * FROM users WHERE email = :email AND pass = :pass LIMIT 1")
    fun getUser(email: String, pass: String): UserItem?

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    fun getUserEmail(email: String): UserItem?

    @Update
    fun updateUser(user: UserItem)

    @Query("SELECT * FROM users WHERE is_auth = :isAuth LIMIT 1")
    suspend fun getUserByAuthStatus(isAuth: Int): UserItem?

    @Query("SELECT * FROM users WHERE is_admin = :isAdmin LIMIT 1")
    suspend fun getUserByAdminStatus(isAdmin: Int): UserItem?

    @Query("SELECT * FROM users WHERE email = :email AND is_admin = 1 LIMIT 1")
    suspend fun getUserByAdminStatus2(email: String): UserItem?

    @Query("SELECT * FROM users WHERE is_admin = 1")
    fun getUsersByAdminStatus(): List<UserItem>


    @Query("SELECT * FROM users WHERE is_auth = 1 LIMIT 1")
    fun getCurrentUser(): UserItem?


}
