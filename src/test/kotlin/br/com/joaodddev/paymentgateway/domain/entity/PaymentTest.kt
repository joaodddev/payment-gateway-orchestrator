package br.com.joaodddev.paymentgateway.domain.entity

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal

class PaymentTest {

    @Test
    fun `should start with PENDING status`() {
        val payment = buildPayment()
        assertEquals(PaymentStatus.PENDING, payment.status)
    }

    @Test
    fun `should transition to PROCESSING`() {
        val payment = buildPayment()
        payment.startProcessing()
        assertEquals(PaymentStatus.PROCESSING, payment.status)
    }

    @Test
    fun `should transition to APPROVED from PROCESSING`() {
        val payment = buildPayment()
        payment.startProcessing()
        payment.approve()
        assertEquals(PaymentStatus.APPROVED, payment.status)
    }

    @Test
    fun `should transition to FAILED from PENDING`() {
        val payment = buildPayment()
        payment.fail("Insufficient funds")
        assertEquals(PaymentStatus.FAILED, payment.status)
        assertEquals("Insufficient funds", payment.failureReason)
    }

    @Test
    fun `should transition to FAILED from PROCESSING`() {
        val payment = buildPayment()
        payment.startProcessing()
        payment.fail("Provider error")
        assertEquals(PaymentStatus.FAILED, payment.status)
    }

    @Test
    fun `should throw when approving non-PROCESSING payment`() {
        val payment = buildPayment()
        assertThrows<IllegalArgumentException> {
            payment.approve()
        }
    }

    @Test
    fun `should throw when processing non-PENDING payment`() {
        val payment = buildPayment()
        payment.startProcessing()
        assertThrows<IllegalArgumentException> {
            payment.startProcessing()
        }
    }

    @Test
    fun `should throw when failing terminal payment`() {
        val payment = buildPayment()
        payment.startProcessing()
        payment.approve()
        assertThrows<IllegalArgumentException> {
            payment.fail("Too late")
        }
    }

    @Test
    fun `should return true for isPending`() {
        assertTrue(buildPayment().isPending())
    }

    @Test
    fun `should return true for isTerminal when APPROVED`() {
        val payment = buildPayment()
        payment.startProcessing()
        payment.approve()
        assertTrue(payment.isTerminal())
    }

    @Test
    fun `should return true for isTerminal when FAILED`() {
        val payment = buildPayment()
        payment.fail("error")
        assertTrue(payment.isTerminal())
    }

    private fun buildPayment() = Payment(
        idempotencyKey = "key-001",
        payerId = "payer-1",
        payeeId = "payee-2",
        amount = BigDecimal("150.00"),
        currency = "BRL"
    )
}