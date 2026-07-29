package br.com.joaodddev.paymentgateway.infrastructure.persistence

import br.com.joaodddev.paymentgateway.domain.entity.Payment
import br.com.joaodddev.paymentgateway.domain.entity.PaymentStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface JpaPaymentRepository : JpaRepository<Payment, Long> {
    fun findByIdempotencyKey(key: String): Payment?
    fun findByPayerId(payerId: String): List<Payment>
    fun findByStatus(status: PaymentStatus): List<Payment>
    fun existsByIdempotencyKey(key: String): Boolean
}