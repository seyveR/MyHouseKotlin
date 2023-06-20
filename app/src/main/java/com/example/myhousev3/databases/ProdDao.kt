package com.example.myhousev3.databases

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ProdDao {
    @Insert
    fun insertProd(prod: ProdItem)

    @Query("SELECT * FROM products")
    fun getAllProd(): Flow<List<ProdItem>>

    @Update
    fun updateProd(prod: ProdItem)

    @Query("SELECT * FROM products WHERE name = :prodName")
    suspend fun getProductsByName(prodName: String): List<ProdItem>

    @Query("SELECT * FROM products WHERE name = :productName LIMIT 1")
    suspend fun getProductByName(productName: String): ProdItem?

    @Query("""
    SELECT p.*
    FROM products p
    INNER JOIN (
        SELECT name, MIN(info) as minWeight
        FROM products
        GROUP BY name
    ) groupedProducts
    ON p.name = groupedProducts.name AND p.info = groupedProducts.minWeight
""")
    fun getUniqueNameLowestWeight(): Flow<List<ProdItem>>

    @Query("SELECT * FROM products WHERE type = :typeName")
    fun getProductsByType(typeName: String): Flow<List<ProdItem>>

//    @Query("SELECT * FROM users WHERE is_auth = :isAuth LIMIT 1")
//    suspend fun getUserByAuthStatus(isAuth: Int): UserItem?
//
//    @Query("SELECT * FROM users WHERE is_auth = 1 LIMIT 1")
//    fun getCurrentUser(): UserItem?


}