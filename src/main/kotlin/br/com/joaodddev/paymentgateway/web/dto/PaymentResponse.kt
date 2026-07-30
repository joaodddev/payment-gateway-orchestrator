package br.com.joaodddev.paymentgateway.web.dto

import br.com.joaodddev.paymentgateway.domain.entity.Payment
import br.com.joaodddev.paymentgateway.domain.entity.PaymentStatus
import java.math.BigDecimal
import java.time.LocalDateTime

data class PaymentResponse(
    val id: Long?,
    val idempotencyKey: String,
    val payerId: String,
    val payeeId: String,
    val amount: BigDecimal,
    val currency: String,
    val status: PaymentStatus,
    val description: String?,
    val failureReason: String?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    companion object {
        fun from(payment: Payment) = PaymentResponse(
            id = payment.id,
            idempotencyKey = payment.idempotencyKey,
            payerId = payment.payerId,
            payeeId = payment.payeeId,
            amount = payment.amount,
            currency = payment.currency,
            status = payment.status,
            description = payment.description,
            failureReason = payment.failureReason,
            createdAt = payment.createdAt,
            updatedAt = payment.updatedAt
        )
    }
}