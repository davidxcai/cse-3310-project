package com.example.mysecondapp.data.db.entity

import androidx.room.Embedded
import androidx.room.Relation

data class ListingWithSeller(
    @Embedded val listing: ListingEntity,
    @Relation(
        parentColumn = "seller_id",
        entityColumn = "id"
    )
    val seller: UserEntity?
)
