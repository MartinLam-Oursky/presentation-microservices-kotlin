package com.oursky.presentation.microservices.kotlin.product.service

import java.util.Date
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.JWT
import com.auth0.jwt.exceptions.JWTVerificationException

@Service
class JwtService {
    private val log = LoggerFactory.getLogger(this.javaClass.name)

    @Value("\${APP_MERCHANT_JWT_SECRET}")
    private val merchantJwtSecret: String = ""

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