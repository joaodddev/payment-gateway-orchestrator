package br.com.joaodddev.paymentgateway.infrastructure.persistence

import br.com.joaodddev.paymentgateway.domain.entity.PaymentEvent
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface JpaPaymentEventRepository : JpaRepository<PaymentEvent, Long> {
    fun findByPaymentId(paymentId: Long): List<PaymentEvent>
}