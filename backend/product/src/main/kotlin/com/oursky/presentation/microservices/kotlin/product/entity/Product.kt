package com.oursky.presentation.microservices.kotlin.product.entity

import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Column

@Entity
data class Product(
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
        val id: Long = -1,

    @Column(nullable = false)
        val ownerID: Long = -1,

    @Column(nullable = false, unique = false, length = 256)
        val name: String = "",

    @Column(nullable = true, unique = false, length = 256)
        val description: String? = "",

    @Column(nullable = false)
        val price: Float = 0f,

    @Column(nullable = true)
        val image: String? = "",

    @Column(nullable = false)
        val stripeProductID: String = "",

    @Column(nullable = false)
        val stripeSKUID: String = "",

    val enabled: Boolean = true
)