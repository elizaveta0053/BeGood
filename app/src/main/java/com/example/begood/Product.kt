package com.example.begood

data class Product(
    val id: Int,
    val title: String,
    val rating: Double,
    val location: String,
    val status: String,
    val imageResId: Int,
    var isFavorite: Boolean = false
)
