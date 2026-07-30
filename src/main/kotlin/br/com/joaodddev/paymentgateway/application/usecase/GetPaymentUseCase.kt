package br.com.joaodddev.paymentgateway.application.usecase

import br.com.joaodddev.paymentgateway.domain.entity.PaymentStatus
import br.com.joaodddev.paymentgateway.domain.repository.PaymentRepository
import br.com.joaodddev.paymentgateway.web.dto.PaymentResponse
import br.com.joaodddev.paymentgateway.web.dto.PaymentStatusResponse
import org.springframework.stereotype.Service

@Service
class GetPaymentUseCase(
    private val paymentRepository: PaymentRepository
) {

    fun findById(id: Long): PaymentResponse {
        val payment = paymentRepository.findById(id)
            ?: throw NoSuchElementException("Payment not found with id=$id")
        return PaymentResponse.from(payment)
    }

    fun findAll(): List<PaymentResponse> =
        paymentRepository.findAll().map { PaymentResponse.from(it) }

    fun findByPayerId(payerId: String): List<PaymentResponse> =
        paymentRepository.findByPayerId(payerId).map { PaymentResponse.from(it) }

    fun findByStatus(status: PaymentStatus): List<PaymentResponse> =
        paymentRepository.findByStatus(status).map { PaymentResponse.from(it) }

    fun getStatus(id: Long): PaymentStatusResponse {
        val payment = paymentRepository.findById(id)
            ?: throw NoSuchElementException("Payment not found with id=$id")
        return PaymentStatusResponse(
            id = payment.id,
            idempotencyKey = payment.idempotencyKey,
            status = payment.status,
            failureReason = payment.failureReason,
            updatedAt = payment.updatedAt
        )
    }
}