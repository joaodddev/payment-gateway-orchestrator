package br.com.joaodddev.paymentgateway.infrastructure.messaging

import br.com.joaodddev.paymentgateway.domain.repository.PaymentRepository
import br.com.joaodddev.paymentgateway.infrastructure.messaging.dto.PaymentCreatedEvent
import br.com.joaodddev.paymentgateway.infrastructure.messaging.dto.PaymentFailedEvent
import br.com.joaodddev.paymentgateway.infrastructure.messaging.dto.PaymentProcessedEvent
import br.com.joaodddev.paymentgateway.infrastructure.resilience.PaymentProcessingService
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.handler.annotation.Header
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class PaymentConsumer(
    private val paymentRepository: PaymentRepository,
    private val paymentProducer: PaymentProducer,
    private val paymentProcessingService: PaymentProcessingService
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @KafkaListener(
        topics = ["\${app.kafka.topics.payment-created}"],
        groupId = "\${spring.kafka.consumer.group-id}"
    )
    @Transactional
    fun consumePaymentCreated(
        @Payload event: PaymentCreatedEvent,
        @Header(KafkaHeaders.RECEIVED_PARTITION) partition: Int,
        @Header(KafkaHeaders.OFFSET) offset: Long
    ) {
        log.info("Consuming PaymentCreatedEvent paymentId=${event.paymentId} partition=$partition offset=$offset")

        val payment = paymentRepository.findById(event.paymentId)
            ?: run {
                log.error("Payment not found for id=${event.paymentId}")
                return
            }

        if (!payment.isPending()) {
            log.warn("Payment ${event.paymentId} is not PENDING, skipping")
            return
        }

        payment.startProcessing()
        paymentRepository.save(payment)

        val result = paymentProcessingService.process(
            paymentId = payment.id!!,
            amount = payment.amount,
            currency = payment.currency
        )

        if (result.success) {
            payment.approve()
            paymentRepository.save(payment)

            paymentProducer.publishPaymentProcessed(
                PaymentProcessedEvent(
                    paymentId = payment.id!!,
                    idempotencyKey = payment.idempotencyKey,
                    status = payment.status.name
                )
            )
            log.info("Payment ${payment.id} approved successfully")
        } else {
            payment.fail(result.failureReason ?: "Unknown error")
            paymentRepository.save(payment)

            paymentProducer.publishPaymentFailed(
                PaymentFailedEvent(
                    paymentId = payment.id!!,
                    idempotencyKey = payment.idempotencyKey,
                    reason = result.failureReason ?: "Unknown error"
                )
            )
            log.error("Payment ${payment.id} failed: ${result.failureReason}")
        }
    }
}