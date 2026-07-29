package br.com.joaodddev.paymentgateway.domain.repository

import br.com.joaodddev.paymentgateway.domain.entity.Payment
import br.com.joaodddev.paymentgateway.domain.entity.PaymentStatus

interface PaymentRepository {
    fun save(payment: Payment): Payment
    fun findById(id: Long): Payment?
    fun findByIdempotencyKey(key: String): Payment?
    fun findByPayerId(payerId: String): List<Payment>
    fun findByStatus(status: PaymentStatus): List<Payment>
    fun findAll(): List<Payment>
    fun existsByIdempotencyKey(key: String): Boolean
}