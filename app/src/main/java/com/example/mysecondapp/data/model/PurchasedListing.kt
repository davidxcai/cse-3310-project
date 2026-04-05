package com.example.mysecondapp.data.model

data class PurchasedListing(
    val id: Long,
    val name: String,
    val price: Float,
    val condition: String,
    val sellerName: String,
    val purchaseDate: Long
)
