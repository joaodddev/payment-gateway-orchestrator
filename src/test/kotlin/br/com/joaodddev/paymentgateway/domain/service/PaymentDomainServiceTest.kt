package br.com.joaodddev.paymentgateway.domain.service

import br.com.joaodddev.paymentgateway.domain.entity.Payment
import br.com.joaodddev.paymentgateway.domain.valueobject.Money
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal

class PaymentDomainServiceTest {

    private val domainService = PaymentDomainService()

    @Test
    fun `should validate payment successfully`() {
        assertDoesNotThrow {
            domainService.validatePayment(buildPayment(BigDecimal("100.00")))
        }
    }

    @Test
    fun `should throw when amount is below minimum`() {
        assertThrows<IllegalArgumentException> {
            domainService.validatePayment(buildPayment(BigDecimal("0.00")))
        }
    }

    @Test
    fun `should throw when amount exceeds maximum`() {
        assertThrows<IllegalArgumentException> {
            domainService.validatePayment(buildPayment(BigDecimal("50001.00")))
        }
    }

    @Test
    fun `should throw when payer and payee are the same`() {
        assertThrows<IllegalArgumentException> {
            domainService.validatePayment(
                Payment(
                    idempotencyKey = "key-001",
                    payerId = "same-id",
                    payeeId = "same-id",
                    amount = BigDecimal("100.00"),
                    currency = "BRL"
                )
            )
        }
    }

    @Test
    fun `should calculate fee as 1 percent`() {
        val money = Money(BigDecimal("100.00"))
        val fee = domainService.calculateFee(money)
        assertEquals(BigDecimal("1.00"), fee)
    }

    @Test
    fun `should return true when payment is eligible for processing`() {
        assertTrue(domainService.isEligibleForProcessing(buildPayment(BigDecimal("100.00"))))
    }

    private fun buildPayment(amount: BigDecimal) = Payment(
        idempotencyKey = "key-001",
        payerId = "payer-1",
        payeeId = "payee-2",
        amount = amount,
        currency = "BRL"
    )
}