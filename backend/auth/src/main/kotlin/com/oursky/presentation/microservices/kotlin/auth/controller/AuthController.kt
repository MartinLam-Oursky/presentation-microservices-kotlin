package com.oursky.presentation.microservices.kotlin.auth.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import com.oursky.presentation.microservices.kotlin.auth.service.AuthService
import com.oursky.presentation.microservices.kotlin.auth.service.JwtService
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    lateinit var authService: AuthService
    @Autowired
    lateinit var jwtService: JwtService

    data class SignupRequest(
        val user: String,
        val pass: String
    )
    data class SignupResponse(
        val userId: Long?,
        val accessToken: String?,
        val error: String?
    )

    // curl -X POST http://127.0.0.1:8080/auth/signup -H "Content-Type: application/json" -d '{"user": "test", "pass": "1234"}'
    @CrossOrigin(origins = ["http://localhost:3000"])
    @PostMapping("/signup")
    fun signup(
        @RequestBody body: SignupRequest
    ): ResponseEntity<SignupResponse> {

        if (authService.usernameExists((body.user))) {
            return ResponseEntity.status(406).body(SignupResponse(
                error = "Username already exists.",
                userId = null,
                accessToken = null
            ))
        }

        val userId = authService.signup(body.user, body.pass)
            ?: return ResponseEntity(HttpStatus.UNAUTHORIZED)

        return ResponseEntity.ok(SignupResponse(
            userId = userId,
            accessToken = jwtService.sign(userId, "access"),
            error = null
        ))
    }

    data class LoginRequest(
        val user: String,
        val pass: String
    )
    data class LoginResponse(
        val userId: Long?,
        val accessToken: String?,
        val error: String?
    )

    // curl -X POST http://127.0.0.1:8080/auth/login -H "Content-Type: application/json" -d '{"user": "test", "pass": "1234"}'
    @CrossOrigin(origins = ["http://localhost:3000"])
    @PostMapping("/login")
    fun login(
        @RequestBody body: LoginRequest
    ): ResponseEntity<LoginResponse> {
        val userId = authService.login(body.user, body.pass)
            ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(LoginResponse(
                userId = null,
                accessToken = null,
                error = "Incorrect username or password."
            ))
        return ResponseEntity.ok(LoginResponse(
            userId = userId,
            accessToken = jwtService.sign(userId, "access"),
            error = null
        ))
    }

    // curl -X GET http://127.0.0.1:8080/auth/logout
    @CrossOrigin(origins = ["http://localhost:3000"])
    @GetMapping("/logout")
    fun logout(): ResponseEntity<Void> {
        return ResponseEntity.ok().build()
    }

    data class VerifyResponse(
        val userId: Long
    )
    // curl -X GET http://127.0.0.1:8080/auth/verify -H "Authorization: Bearer ACCESS_TOKEN"
    @CrossOrigin(origins = ["http://localhost:3000"])
    @GetMapping("/verify")
    fun verify(
        @RequestHeader("authorization") authorization: String
    ): ResponseEntity<VerifyResponse> {
        val jwt = authorization.replace("Bearer ", "", true)
        val userId = jwtService.verify(jwt)
            ?: return ResponseEntity(HttpStatus.UNAUTHORIZED)
        return ResponseEntity.ok(VerifyResponse(
            userId = userId
        ))
    }
}
