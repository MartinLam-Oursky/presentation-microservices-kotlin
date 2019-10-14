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
import org.springframework.web.bind.annotation.PutMapping
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

    data class UpdateProductResponse(
        val success: Boolean,
        val productID: Long?,
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

        val userID = jwtService.merchantVerify(jwt)
            ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(AddProductResponse(
                productId = null,
                error = "Incorrect access token"
            ))

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
        return ResponseEntity.ok(AllProductResponse(
            products = productService.getAll()
        ))
    }

    @CrossOrigin(origins = ["http://localhost:3000"])
    @PutMapping("/{id}")
    fun updateProduct(
        @PathVariable id: Long,
        @RequestHeader("authorization") authorization: String,
        @RequestParam("name") name: String,
        @RequestParam("description") description: String,
        @RequestParam("price") price: Float,
        @RequestParam("enabled", required = false) enabled: Boolean
    ): ResponseEntity<UpdateProductResponse> {

        val jwt = authorization.replace("Bearer ", "", true)

        val userID = jwtService.merchantVerify(jwt)
            ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(UpdateProductResponse(
                success = false,
                productID = null,
                error = "Incorrect access token"
            ))

        if (!productService.isProductExists(id)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(UpdateProductResponse(
                success = false,
                error = "Product does not exists.",
                productID = null
            ))
        }

        if (!productService.isProductCreatedByThisUser(userID, id)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(UpdateProductResponse(
                success = false,
                error = "You do not own this product",
                productID = null
            ))
        }

        val productId = productService.updateProduct(id, name, description, price, enabled)
            ?: return ResponseEntity(HttpStatus.BAD_REQUEST)

        return ResponseEntity.ok(UpdateProductResponse(
            success = productId == id,
            productID = productId,
            error = ""
        ))
    }

    @CrossOrigin(origins = ["http://localhost:3000"])
    @DeleteMapping("/{id}")
    fun deleteProduct(
        @PathVariable id: Long,
        @RequestHeader("authorization") authorization: String
    ): ResponseEntity<DeleteProductResponse> {

        val jwt = authorization.replace("Bearer ", "", true)
        val userID = jwtService.merchantVerify(jwt)
            ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(DeleteProductResponse(
                success = false,
                error = "Incorrect access token"
            ))

        if (!productService.isProductExists(id)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(DeleteProductResponse(
                success = false,
                error = "Product does not exists"
            ))
        }

        if (!productService.isProductCreatedByThisUser(userID, id)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(DeleteProductResponse(
                success = false,
                error = "You do not own this product"
            ))
        }

        return ResponseEntity.ok(DeleteProductResponse(
            success = productService.deleteProduct(id),
            error = ""
        ))
    }
}
