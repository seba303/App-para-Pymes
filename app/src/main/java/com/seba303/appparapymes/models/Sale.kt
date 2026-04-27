package com.seba303.appparapymes.models

data class Sale(
    val id: Long = 0,
    val productId: Long,
    val productName: String,
    val quantity: Int,
    val total: Double,
    val date: String,
    val customerName: String = ""
)
