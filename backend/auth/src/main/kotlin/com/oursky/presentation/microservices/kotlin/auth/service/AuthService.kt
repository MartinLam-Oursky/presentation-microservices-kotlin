package com.oursky.presentation.microservices.kotlin.auth.service

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import com.oursky.presentation.microservices.kotlin.auth.entity.Auth
import com.oursky.presentation.microservices.kotlin.auth.repository.AuthRepository

@Service
class AuthService {
    private val log = LoggerFactory.getLogger(this.javaClass.name)

    @Autowired
    lateinit var repository: AuthRepository

    fun usernameExists(username: String): Boolean {
        try{
            repository.findByUsername(username)
            return true
        }catch(e: EmptyResultDataAccessException){
            return false
        }
    }

    fun signup(username: String, password: String): Long? {
        try {
            val hashed = BCryptPasswordEncoder().encode(password)
            val auth = repository.save(Auth(username = username, password = hashed, enabled = true))
            return auth.id
        } catch (e: DataIntegrityViolationException) {
            return null
        }
    }

    fun login(username: String, password: String): Long? {
        try {
            val auth = repository.findByUsername(username)
            if (!BCryptPasswordEncoder().matches(password, auth.password)) return null
            return auth.id
        } catch (e: EmptyResultDataAccessException) {
            return null
        }
    }
}