package br.com.joaodddev.paymentgateway.web.dto

data class AuthResponse(
    val token: String,
    val email: String
)