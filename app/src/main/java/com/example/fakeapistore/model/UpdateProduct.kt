package com.example.fakeapistore.model

data class UpdateProduct(
    var title: String,
    var price:Double,
    var description: String,
    var image: String,
    var category: String
)
