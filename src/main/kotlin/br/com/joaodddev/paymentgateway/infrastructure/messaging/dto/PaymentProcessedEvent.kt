package br.com.joaodddev.paymentgateway.infrastructure.messaging.dto

import java.time.LocalDateTime

data class PaymentProcessedEvent(
    val paymentId: Long,
    val idempotencyKey: String,
    val status: String,
    val processedAt: LocalDateTime = LocalDateTime.now()
)