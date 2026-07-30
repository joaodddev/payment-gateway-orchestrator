package br.com.joaodddev.paymentgateway.web.dto

import br.com.joaodddev.paymentgateway.domain.entity.PaymentStatus
import java.time.LocalDateTime

data class PaymentStatusResponse(
    val id: Long?,
    val idempotencyKey: String,
    val status: PaymentStatus,
    val failureReason: String?,
    val updatedAt: LocalDateTime
)