package com.oursky.presentation.microservices.kotlin.auth.service

import java.util.Date
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.JWT
import com.auth0.jwt.exceptions.JWTVerificationException

@Service
class JwtService {
    private val jwtSecret: String = System.getenv("APP_JWT_SECRET") ?: ""
    private val merchantJwtSecret: String = System.getenv("APP_MERCHANT_JWT_SECRET") ?: ""

    private val jwtIssuer = "demo"
    private val jwtAccessLifetime = 10 * 60 // 10 minutes
    private val jwtRefreshLifetime = 8 * 60 * 60 // 8 hours
    private val jwtAlgorithm by lazy { Algorithm.HMAC256(jwtSecret) }
    private val merchantJwtAlgorithm by lazy { Algorithm.HMAC256(merchantJwtSecret) }

    constructor(){
        println("\n\n\n\nJWT in auth\n\n\n")
        println(jwtSecret)
        println("\n\n\n\n\n\n\n")
    }

    private val jwtVerifier by lazy {
        JWT.require(jwtAlgorithm)
            .withIssuer(jwtIssuer)
            .build()
    }

    private val merchantJwtVerifier by lazy {
        JWT.require(merchantJwtAlgorithm)
            .withIssuer(jwtIssuer)
            .build()
    }

    fun sign(userId: Long, scope: String): String {
        val now = System.currentTimeMillis()
        return JWT.create()
            .withClaim("user_id", userId)
            .withClaim("scope", scope)
            .withIssuer(jwtIssuer)
            .withIssuedAt(Date(now))
            .withExpiresAt(Date(now + (jwtAccessLifetime * 1000)))
            .sign(jwtAlgorithm)
    }

    fun merchantSign(userId: Long, scope: String): String {
        val now = System.currentTimeMillis()
        return JWT.create()
            .withClaim("user_id", userId)
            .withClaim("scope", scope)
            .withIssuer(jwtIssuer)
            .withIssuedAt(Date(now))
            .withExpiresAt(Date(now + (jwtAccessLifetime * 1000)))
            .sign(merchantJwtAlgorithm)
    }

    fun verify(jwt: String): Long? {
        return try {
            val decoded = jwtVerifier.verify(jwt)
            decoded.getClaim("user_id").asLong()
        } catch (e: JWTVerificationException) {
            null
        }
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