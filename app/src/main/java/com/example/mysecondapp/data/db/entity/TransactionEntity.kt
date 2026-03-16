package com.example.mysecondapp.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.sql.Date

@Entity(
    tableName = "transactions",
    indices = [Index(value = ["buyer_id"])]
)
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "transaction_id")
    val id: Long = 0,

    @ColumnInfo(name = "buyer_id")
    val buyerId: Long,

    @ColumnInfo(name = "listing_id")
    val listingId: Long,

    @ColumnInfo(name = "purchase_date")
    val purchaseDate: Long = System.currentTimeMillis()
)