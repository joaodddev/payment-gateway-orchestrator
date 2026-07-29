package br.com.joaodddev.paymentgateway.infrastructure.messaging

import br.com.joaodddev.paymentgateway.infrastructure.messaging.dto.PaymentCreatedEvent
import br.com.joaodddev.paymentgateway.infrastructure.messaging.dto.PaymentFailedEvent
import br.com.joaodddev.paymentgateway.infrastructure.messaging.dto.PaymentProcessedEvent
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class PaymentProducer(
    private val kafkaTemplate: KafkaTemplate<String, Any>,
    @Value("\${app.kafka.topics.payment-created}") private val paymentCreatedTopic: String,
    @Value("\${app.kafka.topics.payment-processed}") private val paymentProcessedTopic: String,
    @Value("\${app.kafka.topics.payment-failed}") private val paymentFailedTopic: String
) {

    private val log = LoggerFactory.getLogger(javaClass)

    fun publishPaymentCreated(event: PaymentCreatedEvent) {
        log.info("Publishing PaymentCreatedEvent for paymentId=${event.paymentId}")
        kafkaTemplate.send(paymentCreatedTopic, event.idempotencyKey, event)
            .whenComplete { result, ex ->
                if (ex != null) {
                    log.error("Failed to publish PaymentCreatedEvent: ${ex.message}", ex)
                } else {
                    log.info("PaymentCreatedEvent published to partition=${result.recordMetadata.partition()}")
                }
            }
    }

    fun publishPaymentProcessed(event: PaymentProcessedEvent) {
        log.info("Publishing PaymentProcessedEvent for paymentId=${event.paymentId}")
        kafkaTemplate.send(paymentProcessedTopic, event.idempotencyKey, event)
            .whenComplete { _, ex ->
                if (ex != null) log.error("Failed to publish PaymentProcessedEvent: ${ex.message}", ex)
            }
    }

    fun publishPaymentFailed(event: PaymentFailedEvent) {
        log.info("Publishing PaymentFailedEvent for paymentId=${event.paymentId}")
        kafkaTemplate.send(paymentFailedTopic, event.idempotencyKey, event)
            .whenComplete { _, ex ->
                if (ex != null) log.error("Failed to publish PaymentFailedEvent: ${ex.message}", ex)
            }
    }
}