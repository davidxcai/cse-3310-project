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
            Triple("Old Laptop", 100f, "Bob"),
            Triple("Microsoft Surface Laptop 7", 1500f, "Charlie"),
            Triple("Mechanical Keyboard", 150f, "Dylan"),
            Triple("Alienware 16 Aurora", 1220f, "Emma"),
            Triple("HP Laptop 2020", 400f, "Frank"),
            Triple("ASUS Notebook E210", 580f, "Grace"),
            Triple("MSI GF63 Laptop", 1050f, "Henry"),
            Triple("Wireless Mouse", 60f, "Ivy"),
            Triple("Macbook Neo", 450f, "Jack"),
            Triple("External SSD 1TB", 110f, "Karen"),
            Triple("Acer Chromebook", 200f, "Leo"),
            Triple("HP ProBook 465", 500f, "Maya")
        )

        val imageUrls = listOf(
            "https://images.unsplash.com/photo-1517336714731-489689fd1ca8",
            "https://hips.hearstapps.com/pop.h-cdn.co/assets/16/17/1461938344-lte.jpg?resize=980:*",
            "https://cdn.cs.1worldsync.com/bd/99/bd99d5c1-fbc7-4ee3-b448-f59b129fe770.jpg",
            "https://images.unsplash.com/photo-1517433456452-f9633a875f6f",
            "https://i.dell.com/is/image/DellContent/content/dam/ss2/product-images/page/alienware/laptops/aw-16-aurora-ac16250-ac16251-gaming-laptops-overhead-2x1-1.psd?fmt=jpg&wid=800&hei=400",
            "https://m.media-amazon.com/images/I/419cAD-aGYL._AC_UF894,1000_QL80_.jpg",
            "https://m.media-amazon.com/images/I/71Hy5SYr3tL.jpg",
            "https://m.media-amazon.com/images/I/518Lcp3eegL.jpg",
            "https://images.unsplash.com/photo-1527814050087-3793815479db",
            "https://media.cnn.com/api/v1/images/stellar/prod/macbook-neo-hands-on-11-20260304154834551.jpg?c=16x9&q=h_833,w_1480,c_fill",
            "https://encrypted-tbn0.gstatic.com/shopping?q=tbn:ANd9GcSqCd6VfIiQ7s4G0fA2m5vzIFT_hJ3VKut4Vj5h-I3gXF-8FILFZTf_v6wVp8JZzgm8Cvwo8pZ9HudzEvTB5Or3VdYhj2O0xfiti_WBHXcUFDk&usqp=CAc",
            "https://cdn.thewirecutter.com/wp-content/media/2025/08/BEST-CHROMEBOOK-09274.jpg?width=2048&quality=60&crop=2048:1365&auto=webp",
            "https://productimages.microcenter.com/707795_995738_01_front_comping.jpg"
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
