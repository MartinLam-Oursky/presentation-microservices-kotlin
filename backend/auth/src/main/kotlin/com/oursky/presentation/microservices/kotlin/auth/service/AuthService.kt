package com.oursky.presentation.microservices.kotlin.auth.service

import com.oursky.presentation.microservices.kotlin.auth.entity.Auth
import com.oursky.presentation.microservices.kotlin.auth.repository.AuthRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

@Service
class AuthService {
    private val log = LoggerFactory.getLogger(this.javaClass.name)

    @Autowired
    lateinit var authRepository: AuthRepository

    fun isEmailExists(email: String): Boolean {
        try {
            authRepository.findByEmail(email)
            return true
        } catch (e: EmptyResultDataAccessException) {
            return false
        }
    }

    fun signup(email: String, password: String, isMerchant: Boolean): Long? {
        try {
            val hashed = BCryptPasswordEncoder().encode(password)
            val user = authRepository.save(Auth(email = email, password = hashed))
            return user.id
        } catch (e: DataIntegrityViolationException) {
            return null
        }
    }

    fun login(email: String, password: String): Long? {
        try {
            val user = authRepository.findByEmail(email)
            if (!BCryptPasswordEncoder().matches(password, user.password)) return null
            return user.id
        } catch (e: EmptyResultDataAccessException) {
            return null
        }
    }
}