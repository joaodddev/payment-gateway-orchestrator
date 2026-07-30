package br.com.joaodddev.paymentgateway.infrastructure.cache

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class IdempotencyService(
    private val redisTemplate: RedisTemplate<String, Any>,
    @Value("\${app.idempotency.ttl-minutes}") private val ttlMinutes: Long
) {

    private val log = LoggerFactory.getLogger(javaClass)

    companion object {
        private const val KEY_PREFIX = "idempotency:"
    }

    fun isDuplicate(idempotencyKey: String): Boolean {
        val key = buildKey(idempotencyKey)
        val exists = redisTemplate.hasKey(key) == true
        if (exists) log.warn("Duplicate request detected for key=$idempotencyKey")
        return exists
    }

    fun store(idempotencyKey: String, paymentId: Long) {
        val key = buildKey(idempotencyKey)
        redisTemplate.opsForValue().set(key, paymentId.toString(), ttlMinutes, TimeUnit.MINUTES)
        log.debug("Stored idempotency key=$idempotencyKey paymentId=$paymentId TTL=${ttlMinutes}min")
    }

    fun getPaymentId(idempotencyKey: String): Long? {
        val key = buildKey(idempotencyKey)
        val value = redisTemplate.opsForValue().get(key) ?: return null
        return runCatching { value.toString().toLong() }.getOrNull()
    }

    fun evict(idempotencyKey: String) {
        val key = buildKey(idempotencyKey)
        redisTemplate.delete(key)
        log.debug("Evicted idempotency key=$idempotencyKey")
    }

    private fun buildKey(idempotencyKey: String): String = "$KEY_PREFIX$idempotencyKey"
}