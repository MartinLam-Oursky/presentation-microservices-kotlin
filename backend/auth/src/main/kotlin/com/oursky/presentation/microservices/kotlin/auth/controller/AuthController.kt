package com.oursky.presentation.microservices.kotlin.auth.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import com.oursky.presentation.microservices.kotlin.auth.service.UserService
import com.oursky.presentation.microservices.kotlin.auth.service.JwtService
import com.oursky.presentation.microservices.kotlin.auth.service.MerchantService
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.CrossOrigin

@RestController
@RequestMapping("/auth/merchant")
public class MerchantController {
    @Autowired
    lateinit var merchantService: MerchantService
    @Autowired
    lateinit var jwtService: JwtService

    data class SignupRequest(
        val email: String,
        val pass: String
    )
    data class SignupResponse(
        val userId: Long?,
        val accessToken: String?,
        val error: String?
    )

    // curl -X POST http://127.0.0.1:8080/auth/merchant/signup -H "Content-Type: application/json" -d '{"email": "test", "pass": "1234"}'
    @CrossOrigin(origins = ["http://localhost:3000"])
    @PostMapping("/signup")
    fun signup(
        @RequestBody body: SignupRequest
    ): ResponseEntity<SignupResponse> {

        if (merchantService.merchantEmailExists((body.email))) {
            return ResponseEntity.status(406).body(SignupResponse(
                error = "Email already exists.",
                userId = null,
                accessToken = null
            ))
        }

        val userId = merchantService.merchantSignup(body.email, body.pass)
            ?: return ResponseEntity(HttpStatus.UNAUTHORIZED)

        return ResponseEntity.ok(SignupResponse(
            userId = userId,
            accessToken = jwtService.sign(userId, true,  "access"),
            error = null
        ))
    }

    data class LoginRequest(
        val email: String,
        val pass: String
    )
    data class LoginResponse(
        val userId: Long?,
        val accessToken: String?,
        val error: String?
    )

    // curl -X POST http://127.0.0.1:8080/auth/merchant/login -H "Content-Type: application/json" -d '{"email": "test", "pass": "1234"}'
    @CrossOrigin(origins = ["http://localhost:3000"])
    @PostMapping("/login")
    fun login(
        @RequestBody body: LoginRequest
    ): ResponseEntity<LoginResponse> {
        val userId = merchantService.merchantLogin(body.email, body.pass)
            ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(LoginResponse(
                userId = null,
                accessToken = null,
                error = "Incorrect email or password."
            ))
        return ResponseEntity.ok(LoginResponse(
            userId = userId,
            accessToken = jwtService.sign(userId, true, "access"),
            error = null
        ))
    }

    // curl -X GET http://127.0.0.1:8080/auth/merchant/logout
    @CrossOrigin(origins = ["http://localhost:3000"])
    @GetMapping("/logout")
    fun logout(): ResponseEntity<Void> {
        return ResponseEntity.ok().build()
    }

    data class VerifyResponse(
        val userId: Long,
        val isMerchant: Boolean
    )
    // curl -X GET http://127.0.0.1:8080/auth/merchant/verify -H "Authorization: Bearer ACCESS_TOKEN"
    @CrossOrigin(origins = ["http://localhost:3000"])
    @GetMapping("/verify")
    fun verify(
        @RequestHeader("authorization") authorization: String
    ): ResponseEntity<VerifyResponse> {
        val jwt = authorization.replace("Bearer ", "", true)
        val (userId, isMerchant) = jwtService.verify(jwt)
            ?: return ResponseEntity(HttpStatus.UNAUTHORIZED)
        return ResponseEntity.ok(VerifyResponse(
            userId = userId,
            isMerchant = isMerchant
        ))
    }
}

@RestController
@RequestMapping("/auth/user")
public class UserController {
    @Autowired
    lateinit var userService: UserService
    @Autowired
    lateinit var jwtService: JwtService

    data class SignupRequest(
        val email: String,
        val pass: String
    )
    data class SignupResponse(
        val userId: Long?,
        val accessToken: String?,
        val error: String?
    )

    // curl -X POST http://127.0.0.1:8080/auth/user/signup -H "Content-Type: application/json" -d '{"email": "test", "pass": "1234"}'
    @CrossOrigin(origins = ["http://localhost:3000"])
    @PostMapping("/signup")
    fun signup(
            @RequestBody body: SignupRequest
    ): ResponseEntity<SignupResponse> {

        if (userService.userEmailExists((body.email))) {
            return ResponseEntity.status(406).body(SignupResponse(
                error = "Username already exists.",
                userId = null,
                accessToken = null
            ))
        }

        val userId = userService.userSignup(body.email, body.pass)
            ?: return ResponseEntity(HttpStatus.UNAUTHORIZED)

        return ResponseEntity.ok(SignupResponse(
            userId = userId,
            accessToken = jwtService.sign(userId, false, "access"),
            error = null
        ))
    }

    data class LoginRequest(
        val email: String,
        val pass: String
    )
    data class LoginResponse(
        val userId: Long?,
        val accessToken: String?,
        val error: String?
    )

    // curl -X POST http://127.0.0.1:8080/auth/user/login -H "Content-Type: application/json" -d '{"email": "test", "pass": "1234"}'
    @CrossOrigin(origins = ["http://localhost:3000"])
    @PostMapping("/login")
    fun login(
            @RequestBody body: LoginRequest
    ): ResponseEntity<LoginResponse> {
        val userId = userService.userLogin(body.email, body.pass)
            ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(LoginResponse(
                userId = null,
                accessToken = null,
                error = "Incorrect username or password."
            ))
        return ResponseEntity.ok(LoginResponse(
            userId = userId,
            accessToken = jwtService.sign(userId, false, "access"),
            error = null
        ))
    }

    // curl -X GET http://127.0.0.1:8080/auth/user/logout
    @CrossOrigin(origins = ["http://localhost:3000"])
    @GetMapping("/logout")
    fun logout(): ResponseEntity<Void> {
        return ResponseEntity.ok().build()
    }

    data class VerifyResponse(
        val userId: Long,
        val isMerchant: Boolean
    )
    // curl -X GET http://127.0.0.1:8080/auth/user/verify -H "Authorization: Bearer ACCESS_TOKEN"
    @CrossOrigin(origins = ["http://localhost:3000"])
    @GetMapping("/verify")
    fun verify(
        @RequestHeader("authorization") authorization: String
    ): ResponseEntity<VerifyResponse> {
        val jwt = authorization.replace("Bearer ", "", true)
        val (userId, isMerchant) = jwtService.verify(jwt)
            ?: return ResponseEntity(HttpStatus.UNAUTHORIZED)
        return ResponseEntity.ok(VerifyResponse(
            userId = userId,
            isMerchant = isMerchant
        ))
    }
}