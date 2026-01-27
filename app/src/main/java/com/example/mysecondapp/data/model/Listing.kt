package com.example.mysecondapp.data.model

import android.media.Image
import java.util.Date

data class Listing(
    val id: String,
    val name: String,
    val price: Number,
    val seller: String,
    val dateAdded: Date,
    val imageUrl: String,
    val isSold: Boolean
)