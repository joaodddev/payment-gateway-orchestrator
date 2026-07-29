package br.com.joaodddev.paymentgateway.infrastructure.messaging.dto

import java.time.LocalDateTime

data class PaymentFailedEvent(
    val paymentId: Long,
    val idempotencyKey: String,
    val reason: String,
    val failedAt: LocalDateTime = LocalDateTime.now()
)