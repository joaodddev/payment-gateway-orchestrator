package br.com.joaodddev.paymentgateway.application.usecase

import br.com.joaodddev.paymentgateway.domain.entity.Payment
import br.com.joaodddev.paymentgateway.domain.entity.PaymentStatus
import br.com.joaodddev.paymentgateway.domain.repository.PaymentRepository
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal

class GetPaymentUseCaseTest {

    private val paymentRepository = mockk<PaymentRepository>()
    private val useCase = GetPaymentUseCase(paymentRepository)

    @Test
    fun `should find payment by id`() {
        every { paymentRepository.findById(1L) } returns buildPayment()

        val result = useCase.findById(1L)

        assertEquals(1L, result.id)
        assertEquals(PaymentStatus.PENDING, result.status)
    }

    @Test
    fun `should throw when payment not found`() {
        every { paymentRepository.findById(99L) } returns null

        assertThrows<NoSuchElementException> {
            useCase.findById(99L)
        }
    }

    @Test
    fun `should return all payments`() {
        every { paymentRepository.findAll() } returns listOf(buildPayment(), buildPayment())

        val result = useCase.findAll()

        assertEquals(2, result.size)
    }

    @Test
    fun `should return payments by payer`() {
        every { paymentRepository.findByPayerId("payer-1") } returns listOf(buildPayment())

        val result = useCase.findByPayerId("payer-1")

        assertEquals(1, result.size)
        assertEquals("payer-1", result.first().payerId)
    }

    @Test
    fun `should return payment status`() {
        every { paymentRepository.findById(1L) } returns buildPayment()

        val result = useCase.getStatus(1L)

        assertEquals(PaymentStatus.PENDING, result.status)
        assertNull(result.failureReason)
    }

    private fun buildPayment() = Payment(
        id = 1L,
        idempotencyKey = "key-001",
        payerId = "payer-1",
        payeeId = "payee-2",
        amount = BigDecimal("150.00"),
        currency = "BRL",
        status = PaymentStatus.PENDING
    )
}