package br.com.joaodddev.paymentgateway.infrastructure.resilience

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import io.github.resilience4j.retry.annotation.Retry
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class PaymentProcessingService {

    private val log = LoggerFactory.getLogger(javaClass)

    @CircuitBreaker(name = "paymentProcessing", fallbackMethod = "processFallback")
    @Retry(name = "paymentProcessing", fallbackMethod = "processFallback")
    fun process(paymentId: Long, amount: BigDecimal, currency: String): ProcessingResult {
        log.info("Processing payment id=$paymentId amount=$amount $currency")

        simulateExternalProviderCall(paymentId, amount)

        log.info("Payment id=$paymentId processed successfully")
        return ProcessingResult.success(paymentId)
    }

    fun processFallback(
        paymentId: Long,
        amount: BigDecimal,
        currency: String,
        ex: Exception
    ): ProcessingResult {
        log.error("Fallback triggered for paymentId=$paymentId reason=${ex.message}")
        return ProcessingResult.failure(paymentId, ex.message ?: "Processing unavailable")
    }

    private fun simulateExternalProviderCall(paymentId: Long, amount: BigDecimal) {
        log.debug("Calling external payment provider for paymentId=$paymentId")
        Thread.sleep(50)

        if (amount > BigDecimal("49999.00")) {
            throw IllegalArgumentException("Amount exceeds provider limit")
        }
    }
}

data class ProcessingResult(
    val paymentId: Long,
    val success: Boolean,
    val failureReason: String? = null
) {
    companion object {
        fun success(paymentId: Long) = ProcessingResult(paymentId = paymentId, success = true)
        fun failure(paymentId: Long, reason: String) = ProcessingResult(
            paymentId = paymentId,
            success = false,
            failureReason = reason
        )
    }
}