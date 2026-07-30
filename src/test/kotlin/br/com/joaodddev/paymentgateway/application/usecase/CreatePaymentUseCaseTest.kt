package br.com.joaodddev.paymentgateway.application.usecase

import br.com.joaodddev.paymentgateway.domain.entity.Payment
import br.com.joaodddev.paymentgateway.domain.entity.PaymentStatus
import br.com.joaodddev.paymentgateway.domain.repository.PaymentRepository
import br.com.joaodddev.paymentgateway.domain.service.PaymentDomainService
import br.com.joaodddev.paymentgateway.infrastructure.cache.IdempotencyService
import br.com.joaodddev.paymentgateway.infrastructure.messaging.PaymentProducer
import br.com.joaodddev.paymentgateway.infrastructure.messaging.dto.PaymentCreatedEvent
import br.com.joaodddev.paymentgateway.web.dto.CreatePaymentRequest
import io.mockk.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal

class CreatePaymentUseCaseTest {

    private val paymentRepository = mockk<PaymentRepository>()
    private val paymentDomainService = PaymentDomainService()
    private val idempotencyService = mockk<IdempotencyService>()
    private val paymentProducer = mockk<PaymentProducer>()

    private val useCase = CreatePaymentUseCase(
        paymentRepository,
        paymentDomainService,
        idempotencyService,
        paymentProducer
    )

    @Test
    fun `should create payment successfully`() {
        val request = buildRequest()

        every { idempotencyService.isDuplicate("key-001") } returns false
        every { paymentRepository.save(any()) } returns buildPayment()
        every { idempotencyService.store("key-001", 1L) } just Runs
        every { paymentProducer.publishPaymentCreated(any()) } just Runs

        val result = useCase.execute(request, "key-001")

        assertEquals(PaymentStatus.PENDING, result.status)
        assertEquals("payer-1", result.payerId)
        verify(exactly = 1) { paymentRepository.save(any()) }
        verify(exactly = 1) { paymentProducer.publishPaymentCreated(any()) }
        verify(exactly = 1) { idempotencyService.store("key-001", 1L) }
    }

    @Test
    fun `should return existing payment on duplicate request`() {
        val request = buildRequest()
        val existingPayment = buildPayment()

        every { idempotencyService.isDuplicate("key-001") } returns true
        every { idempotencyService.getPaymentId("key-001") } returns 1L
        every { paymentRepository.findById(1L) } returns existingPayment

        val result = useCase.execute(request, "key-001")

        assertEquals(PaymentStatus.PENDING, result.status)
        verify(exactly = 0) { paymentRepository.save(any()) }
        verify(exactly = 0) { paymentProducer.publishPaymentCreated(any()) }
    }

    @Test
    fun `should throw when payer equals payee`() {
        val request = CreatePaymentRequest(
            payerId = "same-id",
            payeeId = "same-id",
            amount = BigDecimal("100.00"),
            currency = "BRL"
        )

        every { idempotencyService.isDuplicate("key-001") } returns false

        assertThrows<IllegalArgumentException> {
            useCase.execute(request, "key-001")
        }

        verify(exactly = 0) { paymentRepository.save(any()) }
    }

    @Test
    fun `should throw when amount exceeds maximum`() {
        val request = CreatePaymentRequest(
            payerId = "payer-1",
            payeeId = "payee-2",
            amount = BigDecimal("99999.00"),
            currency = "BRL"
        )

        every { idempotencyService.isDuplicate("key-001") } returns false

        assertThrows<IllegalArgumentException> {
            useCase.execute(request, "key-001")
        }
    }

    private fun buildRequest() = CreatePaymentRequest(
        payerId = "payer-1",
        payeeId = "payee-2",
        amount = BigDecimal("150.00"),
        currency = "BRL",
        description = "Test payment"
    )

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