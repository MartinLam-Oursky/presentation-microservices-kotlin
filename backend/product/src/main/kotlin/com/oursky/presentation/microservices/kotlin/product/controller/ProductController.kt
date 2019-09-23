package com.oursky.presentation.microservices.kotlin.product.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import com.oursky.presentation.microservices.kotlin.product.service.ProductService
import com.oursky.presentation.microservices.kotlin.product.entity.Product
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.multipart.MultipartHttpServletRequest
import com.oursky.presentation.microservices.kotlin.product.service.JwtService
import org.springframework.web.bind.annotation.*

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

        jwtService.verify(jwt)
            ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(AddProductResponse(
                productId = null,
                error = "Inccorect access token"
            ))

        val productId = productService.addNewProduct(name, description, price, image)
            ?: return ResponseEntity(HttpStatus.BAD_REQUEST)

        return ResponseEntity.ok(AddProductResponse(
            productId = productId,
            error = ""
        ))
    }

    @GetMapping("/")
    fun getAllProduct(): ResponseEntity<AllProductResponse> {
        val data = productService.getAll()
        return ResponseEntity.ok(AllProductResponse(
                products = data
        ))
    }

    @DeleteMapping("/{id}")
    fun deleteProduct(
        @PathVariable id: Long,
        @RequestHeader("authorization") authorization: String
    ): ResponseEntity<DeleteProductResponse> {

        val jwt = authorization.replace("Bearer ", "", true)
        jwtService.verify(jwt)
            ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(DeleteProductResponse(
                success = false,
                error = "Incorrect access token"
            ))

        return ResponseEntity.ok(DeleteProductResponse(
            success = productService.deleteProduct(id),
            error = ""
        ))
    }
}
