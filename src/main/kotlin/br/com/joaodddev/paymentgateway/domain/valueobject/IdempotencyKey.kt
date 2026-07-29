package br.com.joaodddev.paymentgateway.domain.valueobject

import java.util.UUID

data class IdempotencyKey(val value: String) {
    init {
        require(value.isNotBlank()) { "Idempotency key must not be blank" }
        require(value.length <= 64) { "Idempotency key must not exceed 64 characters" }
    }

    companion object {
        fun generate(): IdempotencyKey = IdempotencyKey(UUID.randomUUID().toString())
    }

    override fun toString(): String = value
}