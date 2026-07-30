package br.com.joaodddev.paymentgateway.application.usecase

import br.com.joaodddev.paymentgateway.domain.entity.Payment
import br.com.joaodddev.paymentgateway.domain.repository.PaymentRepository
import br.com.joaodddev.paymentgateway.domain.service.PaymentDomainService
import br.com.joaodddev.paymentgateway.domain.valueobject.IdempotencyKey
import br.com.joaodddev.paymentgateway.infrastructure.cache.IdempotencyService
import br.com.joaodddev.paymentgateway.infrastructure.messaging.PaymentProducer
import br.com.joaodddev.paymentgateway.infrastructure.messaging.dto.PaymentCreatedEvent
import br.com.joaodddev.paymentgateway.web.dto.CreatePaymentRequest
import br.com.joaodddev.paymentgateway.web.dto.PaymentResponse
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CreatePaymentUseCase(
    private val paymentRepository: PaymentRepository,
    private val paymentDomainService: PaymentDomainService,
    private val idempotencyService: IdempotencyService,
    private val paymentProducer: PaymentProducer
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @Transactional
    fun execute(request: CreatePaymentRequest, idempotencyKey: String): PaymentResponse {
        log.info("Creating payment for payer=${request.payerId} amount=${request.amount}")

        if (idempotencyService.isDuplicate(idempotencyKey)) {
            log.warn("Duplicate payment request detected key=$idempotencyKey")
            val existingPaymentId = idempotencyService.getPaymentId(idempotencyKey)
                ?: throw NoSuchElementException("Payment not found for idempotency key")
            val existing = paymentRepository.findById(existingPaymentId)
                ?: throw NoSuchElementException("Payment not found with id=$existingPaymentId")
            return PaymentResponse.from(existing)
        }

        val payment = Payment(
            idempotencyKey = idempotencyKey,
            payerId = request.payerId,
            payeeId = request.payeeId,
            amount = request.amount,
            currency = request.currency,
            description = request.description
        )

        paymentDomainService.validatePayment(payment)

        val saved = paymentRepository.save(payment)
        idempotencyService.store(idempotencyKey, saved.id!!)

        paymentProducer.publishPaymentCreated(
            PaymentCreatedEvent(
                paymentId = saved.id!!,
                idempotencyKey = saved.idempotencyKey,
                payerId = saved.payerId,
                payeeId = saved.payeeId,
                amount = saved.amount,
                currency = saved.currency,
                description = saved.description
            )
        )

        log.info("Payment created id=${saved.id} key=$idempotencyKey")
        return PaymentResponse.from(saved)
    }
}