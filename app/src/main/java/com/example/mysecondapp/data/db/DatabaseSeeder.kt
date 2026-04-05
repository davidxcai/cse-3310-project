package com.example.mysecondapp.data.db

import com.example.mysecondapp.data.db.entity.ListingEntity
import com.example.mysecondapp.data.db.entity.UserEntity
import com.example.mysecondapp.data.db.MarketplaceRepository


object DatabaseSeeder {

    suspend fun seedDatabase(repository: MarketplaceRepository) {
        // 1. Create an Admin user
        repository.registerUser(
            UserEntity(
                name = "Admin User",
                email = "admin@example.com",
                password = "adminpassword",
                accountType = "ADMIN",
                isAdmin = true,
                preferDarkMode = false
            )
        )

        // 2. Create a few Users first (necessary for Foreign Keys)
        val names = listOf("Alice", "Bob", "Charlie", "Dylan", "Emma", "Frank", "Grace", "Henry", "Ivy", "Jack", "Karen", "Leo", "Maya")

        val userMap = mutableMapOf<String, Long>()

        names.forEach { name ->
            val userId = repository.registerUser(
                UserEntity(
                    name = name,
                    email = "${name.lowercase()}@example.com",
                    password = "password123",
                    accountType = "SELLER",
                    isAdmin = false,
                    preferDarkMode = false
                )
            )
            userMap[name] = userId
        }

        // 3. Map your Dummy Data to ListingEntities
        val dummyListings = listOf(
            Triple("Used MacBook Pro", 900f, "Alice"),
            Triple("Gaming PC", 1200f, "Bob"),
            Triple("iPhone 12", 500f, "Charlie"),
            Triple("Mechanical Keyboard", 150f, "Dylan"),
            Triple("Noise Cancelling Headphones", 220f, "Emma"),
            Triple("iPad Air", 400f, "Frank"),
            Triple("Nintendo Switch", 280f, "Grace"),
            Triple("4K Monitor", 350f, "Henry"),
            Triple("Wireless Mouse", 60f, "Ivy"),
            Triple("Desk Lamp", 45f, "Jack"),
            Triple("External SSD 1TB", 110f, "Karen"),
            Triple("Office Chair", 200f, "Leo"),
            Triple("Bluetooth Speaker", 90f, "Maya")
        )

        val imageUrls = listOf(
            "https://images.unsplash.com/photo-1517336714731-489689fd1ca8",
            "https://images.unsplash.com/photo-1593640408182-31c70c8268f5",
            "https://images.unsplash.com/photo-1603899122634-f086ca5f5ddd",
            "https://images.unsplash.com/photo-1517433456452-f9633a875f6f",
            "https://images.unsplash.com/photo-1518441902113-f5c6d3b1d6f3",
            "https://images.unsplash.com/photo-1587825140708-dfaf72ae4b04",
            "https://images.unsplash.com/photo-1587202372775-e229f172b9d8",
            "https://images.unsplash.com/photo-1585792180666-f7347c490ee2",
            "https://images.unsplash.com/photo-1527814050087-3793815479db",
            "https://images.unsplash.com/photo-1507473885765-e6ed057f782c",
            "https://images.unsplash.com/photo-1616627988511-9c5f87c1a710",
            "https://images.unsplash.com/photo-1598300053653-3f8b8c0b6c1c",
            "https://images.unsplash.com/photo-1585386959984-a41552231691"
        )

        dummyListings.forEachIndexed { index, (name, price, sellerName) ->
            repository.createListing(
                ListingEntity(
                    name = name,
                    price = price,
                    sellerId = userMap[sellerName] ?: 1L,
                    dateAdded = System.currentTimeMillis(),
                    imageUrl = imageUrls[index],
                    condition = if (price > 500) "Excellent" else "Good",
                    isSold = (index % 3 == 0), // Mixes up sold/unsold items
                    isHidden = false
                )
            )
        }
    }
}
