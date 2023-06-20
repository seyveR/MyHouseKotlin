package com.example.myhousev3.databases

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {
    @Insert
    fun insertOrder(order: OrderItem)

    @Query("SELECT * FROM `order`")
    fun getAllOrder(): Flow<List<OrderItem>>

    @Update
    fun updateOrder(order: OrderItem)

    @Query("SELECT * FROM `order` WHERE user_info = :userEmail")
    fun getOrderByUser(userEmail: String): Flow<List<OrderItem>>

    @Query("DELETE FROM `order` WHERE id = :orderId")
    suspend fun deleteOrderItem(orderId: Int)

    @Query("DELETE FROM `order` WHERE user_info = :userEmail")
    suspend fun deleteOrderItemsByUser(userEmail: String)

    @Query("UPDATE `order` SET user_info = :newEmail WHERE user_info = :oldEmail")
    fun updateUserInfoByEmail(oldEmail: String, newEmail: String)


}