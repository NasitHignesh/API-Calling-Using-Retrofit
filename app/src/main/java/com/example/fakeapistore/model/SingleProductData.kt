package com.example.fakeapistore.model

import java.io.Serializable

data class SingleProductData(
    val category: String,
    val description: String,
    val id: Int,
    val image: String,
    val price: Double,
    val rating: Rating,
    val title: String
): Serializable