package br.com.joaodddev.paymentgateway.domain.service

import br.com.joaodddev.paymentgateway.domain.entity.Payment
import br.com.joaodddev.paymentgateway.domain.entity.PaymentStatus
import br.com.joaodddev.paymentgateway.domain.valueobject.Money
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class PaymentDomainService {

    companion object {
        val MAX_AMOUNT: BigDecimal = BigDecimal("50000.00")
        val MIN_AMOUNT: BigDecimal = BigDecimal("0.01")
    }

    fun validatePayment(payment: Payment) {
        require(payment.amount >= MIN_AMOUNT) {
            "Amount must be at least $MIN_AMOUNT"
        }
        require(payment.amount <= MAX_AMOUNT) {
            "Amount must not exceed $MAX_AMOUNT"
        }
        require(payment.payerId != payment.payeeId) {
            "Payer and payee must be different"
        }
    }

    fun isEligibleForProcessing(payment: Payment): Boolean =
        payment.status == PaymentStatus.PENDING

    fun calculateFee(money: Money): BigDecimal =
        money.amount
            .multiply(BigDecimal("0.01"))
            .setScale(2, java.math.RoundingMode.HALF_UP)
}