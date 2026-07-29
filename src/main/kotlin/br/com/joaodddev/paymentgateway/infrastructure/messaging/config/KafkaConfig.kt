package br.com.joaodddev.paymentgateway.infrastructure.messaging.config

import org.apache.kafka.clients.admin.NewTopic
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.TopicBuilder

@Configuration
class KafkaConfig {

    @Value("\${app.kafka.topics.payment-created}")
    private lateinit var paymentCreatedTopic: String

    @Value("\${app.kafka.topics.payment-processed}")
    private lateinit var paymentProcessedTopic: String

    @Value("\${app.kafka.topics.payment-failed}")
    private lateinit var paymentFailedTopic: String

    @Bean
    fun paymentCreatedTopic(): NewTopic =
        TopicBuilder.name(paymentCreatedTopic)
            .partitions(3)
            .replicas(1)
            .build()

    @Bean
    fun paymentProcessedTopic(): NewTopic =
        TopicBuilder.name(paymentProcessedTopic)
            .partitions(3)
            .replicas(1)
            .build()

    @Bean
    fun paymentFailedTopic(): NewTopic =
        TopicBuilder.name(paymentFailedTopic)
            .partitions(3)
            .replicas(1)
            .build()
}