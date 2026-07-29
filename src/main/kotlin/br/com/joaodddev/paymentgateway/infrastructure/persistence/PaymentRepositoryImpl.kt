package br.com.joaodddev.paymentgateway.infrastructure.persistence

import br.com.joaodddev.paymentgateway.domain.entity.Payment
import br.com.joaodddev.paymentgateway.domain.entity.PaymentStatus
import br.com.joaodddev.paymentgateway.domain.repository.PaymentRepository
import org.springframework.stereotype.Component

@Component
class PaymentRepositoryImpl(
    private val jpa: JpaPaymentRepository
) : PaymentRepository {
    override fun save(payment: Payment): Payment = jpa.save(payment)
    override fun findById(id: Long): Payment? = jpa.findById(id).orElse(null)
    override fun findByIdempotencyKey(key: String): Payment? = jpa.findByIdempotencyKey(key)
    override fun findByPayerId(payerId: String): List<Payment> = jpa.findByPayerId(payerId)
    override fun findByStatus(status: PaymentStatus): List<Payment> = jpa.findByStatus(status)
    override fun findAll(): List<Payment> = jpa.findAll()
    override fun existsByIdempotencyKey(key: String): Boolean = jpa.existsByIdempotencyKey(key)
}