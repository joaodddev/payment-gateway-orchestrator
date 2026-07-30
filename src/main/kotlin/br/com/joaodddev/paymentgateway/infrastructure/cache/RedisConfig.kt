package br.com.joaodddev.paymentgateway.infrastructure.cache

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.kotlinModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
class RedisConfig {

    @Bean
    fun redisTemplate(connectionFactory: RedisConnectionFactory): RedisTemplate<String, Any> {
        val objectMapper = ObjectMapper().apply {
            registerModule(JavaTimeModule())
            registerModule(kotlinModule())
            disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        }

        val serializer = Jackson2JsonRedisSerializer(objectMapper, Any::class.java)

        return RedisTemplate<String, Any>().apply {
            setConnectionFactory(connectionFactory)
            keySerializer = StringRedisSerializer()
            valueSerializer = serializer
            hashKeySerializer = StringRedisSerializer()
            hashValueSerializer = serializer
        }
    }
}