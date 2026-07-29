package br.com.joaodddev.paymentgateway.domain.repository

import br.com.joaodddev.paymentgateway.domain.entity.PaymentEvent

interface PaymentEventRepository {
    fun save(event: PaymentEvent): PaymentEvent
    fun findByPaymentId(paymentId: Long): List<PaymentEvent>
}