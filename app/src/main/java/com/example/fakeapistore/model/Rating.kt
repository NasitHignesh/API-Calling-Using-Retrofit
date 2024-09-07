package com.example.fakeapistore.model

import java.io.Serializable

data class Rating(
    val count: Int,
    val rate: Double
): Serializable