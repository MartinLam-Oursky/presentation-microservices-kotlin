package com.oursky.presentation.microservices.kotlin.auth.service

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import com.oursky.presentation.microservices.kotlin.auth.entity.Merchant
import com.oursky.presentation.microservices.kotlin.auth.entity.User
import com.oursky.presentation.microservices.kotlin.auth.repository.MerchantRepository
import com.oursky.presentation.microservices.kotlin.auth.repository.UserRepository

@Service
class UserService {
    private val log = LoggerFactory.getLogger(this.javaClass.name)

    @Autowired
    lateinit var userRepository: UserRepository

    fun userEmailExists(email: String): Boolean {
        try {
            userRepository.findByEmail(email)
            return true
        } catch (e: EmptyResultDataAccessException) {
            return false
        }
    }

    fun userSignup(email: String, password: String): Long? {
        try {
            val hashed = BCryptPasswordEncoder().encode(password)
            val user = userRepository.save(User(email = email, password = hashed, enabled = true))
            return user.id
        } catch (e: DataIntegrityViolationException) {
            return null
        }
    }

    fun userLogin(email: String, password: String): Long? {
        try {
            val user = userRepository.findByEmail(email)
            if (!BCryptPasswordEncoder().matches(password, user.password)) return null
            return user.id
        } catch (e: EmptyResultDataAccessException) {
            return null
        }
    }
}

@Service
class MerchantService {
    private val log = LoggerFactory.getLogger(this.javaClass.name)

    @Autowired
    lateinit var merchantRepository: MerchantRepository

    fun merchantEmailExists(email: String): Boolean {
        try {
            merchantRepository.findByEmail(email)
            return true
        } catch (e: EmptyResultDataAccessException) {
            return false
        }
    }

    fun merchantSignup(email: String, password: String): Long? {
        try {
            val hashed = BCryptPasswordEncoder().encode(password)
            val merchant = merchantRepository.save(Merchant(email = email, password = hashed, enabled = true))
            return merchant.id
        } catch (e: DataIntegrityViolationException) {
            return null
        }
    }

    fun merchantLogin(email: String, password: String): Long? {
        try {
            val merchant = merchantRepository.findByEmail(email)
            if (!BCryptPasswordEncoder().matches(password, merchant.password)) return null
            return merchant.id
        } catch (e: EmptyResultDataAccessException) {
            return null
        }
    }

}