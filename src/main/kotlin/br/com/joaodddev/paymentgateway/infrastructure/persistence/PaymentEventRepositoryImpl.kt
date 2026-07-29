package br.com.joaodddev.paymentgateway.infrastructure.persistence

import br.com.joaodddev.paymentgateway.domain.entity.PaymentEvent
import br.com.joaodddev.paymentgateway.domain.repository.PaymentEventRepository
import org.springframework.stereotype.Component

@Component
class PaymentEventRepositoryImpl(
    private val jpa: JpaPaymentEventRepository
) : PaymentEventRepository {
    override fun save(event: PaymentEvent): PaymentEvent = jpa.save(event)
    override fun findByPaymentId(paymentId: Long): List<PaymentEvent> = jpa.findByPaymentId(paymentId)
}