package com.example.myhousev3.databases

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProdItem (
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    @ColumnInfo(name = "name")
    var name: String,
    @ColumnInfo(name = "type")
    var type: String,
    @ColumnInfo(name = "price")
    var price: String,
    @ColumnInfo(name = "info")
    var info: String,
    @ColumnInfo(name = "imageRes")
    var imageRes: String? = null,
    @ColumnInfo(name = "imageCat")
    var imageCat: String? = null
)