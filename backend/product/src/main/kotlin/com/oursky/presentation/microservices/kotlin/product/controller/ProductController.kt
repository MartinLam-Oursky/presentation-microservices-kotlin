package com.oursky.presentation.microservices.kotlin.product.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import com.oursky.presentation.microservices.kotlin.product.service.ProductService
import com.oursky.presentation.microservices.kotlin.product.entity.Product
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.multipart.MultipartHttpServletRequest
import com.oursky.presentation.microservices.kotlin.product.service.JwtService

@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    lateinit var productService: ProductService
    @Autowired
    lateinit var jwtService: JwtService

    data class AddProductResponse(
        val productId: Long?,
        val error: String?
    )

    data class AllProductResponse(
        val products: MutableIterable<Product>
    )

    data class DeleteProductResponse(
        val success: Boolean,
        val error: String?
    )

    @CrossOrigin(origins = ["http://localhost:3000"])
    @PostMapping("/")
    fun add(
        request: MultipartHttpServletRequest,
        @RequestParam("files") image: MultipartFile,
        @RequestParam("name") name: String,
        @RequestParam("description") description: String,
        @RequestParam("price") price: Float,
        @RequestHeader("authorization") authorization: String
    ): ResponseEntity<AddProductResponse> {

        val jwt = authorization.replace("Bearer ", "", true)

        val (userID, isMerchant) = jwtService.verify(jwt)
            ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(AddProductResponse(
                productId = null,
                error = "Incorrect access token"
            ))

        if (!isMerchant) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(AddProductResponse(
                productId = null,
                error = "Incorrect access token"
            ))
        }

        val productId = productService.addNewProduct(name, userID, description, price, image)
            ?: return ResponseEntity(HttpStatus.BAD_REQUEST)

        return ResponseEntity.ok(AddProductResponse(
            productId = productId,
            error = ""
        ))
    }

    @CrossOrigin(origins = ["http://localhost:3000"])
    @GetMapping("/")
    fun getAllProduct(): ResponseEntity<AllProductResponse> {
        val data = productService.getAll()
        return ResponseEntity.ok(AllProductResponse(
                products = data
        ))
    }

    @CrossOrigin(origins = ["http://localhost:3000"])
    @DeleteMapping("/{id}")
    fun deleteProduct(
        @PathVariable id: Long,
        @RequestHeader("authorization") authorization: String
    ): ResponseEntity<DeleteProductResponse> {

        val jwt = authorization.replace("Bearer ", "", true)
        val (_, isMerchant) = jwtService.verify(jwt)
            ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(DeleteProductResponse(
                success = false,
                error = "Incorrect access token"
            ))

        if (!isMerchant) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(DeleteProductResponse(
                success = false,
                error = "Incorrect access token"
            ))
        }

        return ResponseEntity.ok(DeleteProductResponse(
            success = productService.deleteProduct(id),
            error = ""
        ))
    }
}
