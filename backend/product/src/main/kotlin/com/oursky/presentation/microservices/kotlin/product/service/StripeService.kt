package com.oursky.presentation.microservices.kotlin.product.service

import com.stripe.Stripe
import com.stripe.exception.StripeException
import com.stripe.model.Product
import com.stripe.model.Sku
import org.springframework.stereotype.Service
import java.util.ArrayList
import java.util.HashMap

@Service
class StripeService {
    private val stripeSecretKey: String = System.getenv("STRIPE_SECRET_KEY") ?: ""

    constructor(){
        Stripe.apiKey = stripeSecretKey
    }

    fun createProduct(name: String, description: String): String? {
        val attributes = ArrayList<String>()
        attributes.add("name")
        val params = HashMap<String, Any>()
        params["name"] = name
        params["type"] = "good"
        params["description"] = description
        params["attributes"] = attributes
        return try {
            val stripeProduct = Product.create(params)
            stripeProduct.id
        } catch(e : StripeException) {
            println(e.message)
            println(e.cause)
            null
        }
    }

    fun createSKU(productID: String, name: String, price: Float): String? {
        val attributes = HashMap<String, Any>()
        attributes["name"] = name
        val inventory = HashMap<String, Any>()
        inventory["type"] = "infinite"
        val params = HashMap<String, Any>()
        params["attributes"] = attributes
        params["price"] = (price * 100).toInt()
        params["currency"] = "hkd"
        params["inventory"] = inventory
        params["product"] = productID
        return try {
            val stripeSKU = Sku.create(params)
            stripeSKU.id
        } catch(e: StripeException) {
            println(e.message)
            println(e.cause)
            null
        }

    }
}