package com.example.myhousev3.databases

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CatDao {
    @Insert
    fun insertCat(cat: CatItem)

    @Query("SELECT * FROM categories")
    fun getAllCat(): Flow<List<CatItem>>

    @Update
    fun updateCat(cat: CatItem)

    @Query("SELECT * FROM categories WHERE name = :catName")
    suspend fun getCategoriesByName(catName: String): List<CatItem>

//    @Query("""
//    SELECT p.*
//    FROM categories p
//    INNER JOIN (
//        SELECT name, MIN(info) as minWeight
//        FROM categories
//        GROUP BY name
//    ) groupedCategories
//    ON p.name = groupedCategories.name AND p.info = groupedCategories.minWeight
//""")
//    fun getUniqueNameLowestWeight(): Flow<List<CatItem>>




}