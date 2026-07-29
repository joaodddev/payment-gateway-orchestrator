package br.com.joaodddev.paymentgateway.infrastructure.messaging.dto

import java.math.BigDecimal
import java.time.LocalDateTime

data class PaymentCreatedEvent(
    val paymentId: Long,
    val idempotencyKey: String,
    val payerId: String,
    val payeeId: String,
    val amount: BigDecimal,
    val currency: String,
    val description: String?,
    val createdAt: LocalDateTime = LocalDateTime.now()
)