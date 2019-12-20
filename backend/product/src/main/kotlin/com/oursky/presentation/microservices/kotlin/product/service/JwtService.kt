package com.oursky.presentation.microservices.kotlin.product.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.JWT
import com.auth0.jwt.exceptions.JWTVerificationException

@Service
class JwtService {
    private val merchantJwtSecret: String = System.getenv("APP_MERCHANT_JWT_SECRET") ?: ""

    private val jwtIssuer = "demo"
    private val merchantJwtAlgorithm by lazy { Algorithm.HMAC256(merchantJwtSecret) }

    private val merchantJwtVerifier by lazy {
        JWT.require(merchantJwtAlgorithm)
            .withIssuer(jwtIssuer)
            .build()
    }

    fun merchantVerify(jwt: String): Long? {
        return try {
            val decoded = merchantJwtVerifier.verify(jwt)
            decoded.getClaim("user_id").asLong()
        } catch (e: JWTVerificationException) {
            null
        }
    }
}