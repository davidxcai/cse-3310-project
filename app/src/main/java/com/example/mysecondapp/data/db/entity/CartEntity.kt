package com.example.mysecondapp.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "cart",
    primaryKeys = ["buyer_id", "listing_id"], // This pair IS the unique ID now
    indices = [Index(value = ["buyer_id"])]
)
data class CartEntity(
    @ColumnInfo(name = "buyer_id")
    val buyerId: Long,

    @ColumnInfo(name = "listing_id")
    val listingId: Long
)