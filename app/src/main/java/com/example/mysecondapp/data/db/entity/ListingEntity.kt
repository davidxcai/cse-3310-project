package com.example.mysecondapp.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "listing",
    indices = [Index(
        value = ["seller_id"])
    ],
)
data class ListingEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "listing_id")
    val id: Long = 0,

    val name: String,
    val price: Float,

    @ColumnInfo(name = "seller_id")
    val sellerId: Long, // foreign key to userId

    @ColumnInfo(name = "date_added")
    val dateAdded: Long, // is long for simplicity

    @ColumnInfo(name = "image_url")
    val imageUrl: String? = null,

    @ColumnInfo(name = "local_image_path")
    val localImagePath: String? = null,

    val condition: String,

    @ColumnInfo(name = "is_sold")
    val isSold: Boolean = false, // default, item is not yet sold

    @ColumnInfo(name = "is_hidden")
    val isHidden: Boolean = false, // visible by default unless admin or seller changes
)