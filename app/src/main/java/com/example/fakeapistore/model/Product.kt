package com.example.fakeapistore.model

data class Product(
    val category: String,
    val description: String,
    val image: String,
    val price: Double,
    val rating: Rating,
    val title: String
)
