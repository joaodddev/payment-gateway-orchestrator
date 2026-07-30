package br.com.joaodddev.paymentgateway.web.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size
import java.math.BigDecimal

data class CreatePaymentRequest(

    @field:NotBlank(message = "Payer ID is required")
    val payerId: String,

    @field:NotBlank(message = "Payee ID is required")
    val payeeId: String,

    @field:Positive(message = "Amount must be positive")
    val amount: BigDecimal,

    @field:Size(min = 3, max = 3, message = "Currency must be 3 characters")
    val currency: String = "BRL",

    val description: String? = null
)