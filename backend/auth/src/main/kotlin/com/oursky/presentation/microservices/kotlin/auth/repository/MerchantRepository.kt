package com.oursky.presentation.microservices.kotlin.auth.repository

import org.springframework.stereotype.Repository
import org.springframework.data.repository.CrudRepository
import com.oursky.presentation.microservices.kotlin.auth.entity.Merchant

@Repository
interface MerchantRepository : CrudRepository <Merchant, Long> {
    fun findByEmail(email: String): Merchant
}