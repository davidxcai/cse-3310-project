package com.example.mysecondapp.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "cart",
    primaryKeys = ["buyer_id", "listing_id"],
    indices = [Index(
        value = ["buyer_id"])
    ],
)
data class CartEntity(
    @PrimaryKey(autoGenerate = true)

    @ColumnInfo(name = "cart_id")
    val id: Long = 0,

    @ColumnInfo(name = "buyer_id") // foreign key
    val buyerId: Long,

    @ColumnInfo(name = "listing_id") // foreign key
    val listingId: Long,
)