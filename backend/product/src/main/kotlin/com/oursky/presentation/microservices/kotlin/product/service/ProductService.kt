package com.oursky.presentation.microservices.kotlin.product.service

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import com.oursky.presentation.microservices.kotlin.product.entity.Product
import com.oursky.presentation.microservices.kotlin.product.repository.ProductRepository
import org.springframework.web.multipart.MultipartFile
import io.minio.MinioClient
import java.security.MessageDigest
import java.lang.IllegalArgumentException

@Service
class ProductService {
    private val log = LoggerFactory.getLogger(this.javaClass.name)
    private val minioClientEndPoint = "http://172.18.0.1:9000"

    @Autowired
    lateinit var repository: ProductRepository

    val minioClient: MinioClient by lazy {
        MinioClient(
            minioClientEndPoint,
            System.getenv("MINIO_ACCESS_KEY"),
            System.getenv("MINIO_SECRET_KEY")
        )
    }

    constructor() {
        if (!minioClient.bucketExists("images")) {
            minioClient.makeBucket("images")
        }
        minioClient.setBucketPolicy("images", """
            {
                "Statement": [
                    {
                        "Action": [
                            "s3:GetBucketLocation",
                            "s3:ListBucket"
                        ],
                        "Effect": "Allow",
                        "Principal": "*",
                        "Resource": "arn:aws:s3:::images"
                    },
                    {
                        "Action": "s3:GetObject",
                        "Effect": "Allow",
                        "Principal": "*",
                        "Resource": "arn:aws:s3:::images/*"
                    }
                ],
                "Version": "2012-10-17"
            }
        """.trimIndent())
    }

    fun findById(id: Long): Product {
        return repository.findById(id).get()
    }

    fun deleteProduct(id: Long): Boolean {
        try {
            repository.deleteById(id)
            return true
        } catch (e: IllegalArgumentException) {
            return false
        }
    }

    fun updateProduct (
        productID: Long,
        name: String,
        description: String,
        price: Float,
        enable: Boolean
    ): Long? {
        try {
            val product = repository.save(
                Product(
                    id = productID,
                    name = name,
                    description = description,
                    price = price,
                    enabled = enable
                )
            )
            return product.id
        } catch (e: Throwable) {
            println(e.message)
            println(e.cause)
            return null
        }
    }

    fun addNewProduct(
        name: String,
        ownerID: Long,
        description: String,
        price: Float,
        image: MultipartFile
    ): Long? {
        try {
            val currentTimestamp = System.currentTimeMillis().toString()
            val bytes = image.getName().plus(currentTimestamp).toByteArray()
            val md = MessageDigest.getInstance("SHA-1")
            val digest = md.digest(bytes)
            val objectName = digest.fold("", { str, it -> str + "%02x".format(it) })
            val headerMap: HashMap<String, String> = hashMapOf<String, String>(
                "Content-Type" to "application/octet-stream"
            )
            minioClient.putObject(
                "images",
                objectName,
                image.getInputStream(),
                image.getSize(),
                headerMap
            )
            val product = repository.save(
                Product(
                    name = name,
                    description = description,
                    price = price,
                    enabled = true,
                    image = objectName,
                    ownerID = ownerID
                )
            )
            return product.id
        } catch (e: Throwable) {
            println(e.message)
            println(e.cause)
            return null
        }
    }

    fun getAll(): MutableIterable<Product> {
        return repository.findAll()
    }

    fun isProductCreatedByThisUser(userid: Long, productID: Long): Boolean {
        return findById(productID).ownerID == userid
    }

}