package com.oursky.presentation.microservices.kotlin.auth.entity

import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Table
import javax.persistence.Column

@Entity
@Table(name = "merchant")
data class Merchant(
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long = -1,

    @Column(nullable = false, unique = true, length = 256)
    val email: String = "",

    @Column(nullable = false, length = 256)
    val password: String = "",

    val enabled: Boolean = true
)
