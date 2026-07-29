package br.com.joaodddev.paymentgateway.domain.entity

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "payments")
class Payment(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "idempotency_key", nullable = false, unique = true, length = 64)
    val idempotencyKey: String,

    @Column(name = "payer_id", nullable = false, length = 64)
    val payerId: String,

    @Column(name = "payee_id", nullable = false, length = 64)
    val payeeId: String,

    @Column(nullable = false, precision = 18, scale = 2)
    val amount: BigDecimal,

    @Column(nullable = false, length = 3)
    val currency: String = "BRL",

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: PaymentStatus = PaymentStatus.PENDING,

    @Column(length = 255)
    val description: String? = null,

    @Column(name = "failure_reason", length = 255)
    var failureReason: String? = null,

    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
) {
    fun approve() {
        require(status == PaymentStatus.PROCESSING) {
            "Cannot approve a payment with status $status"
        }
        status = PaymentStatus.APPROVED
        updatedAt = LocalDateTime.now()
    }

    fun fail(reason: String) {
        require(status in listOf(PaymentStatus.PENDING, PaymentStatus.PROCESSING)) {
            "Cannot fail a payment with status $status"
        }
        status = PaymentStatus.FAILED
        failureReason = reason
        updatedAt = LocalDateTime.now()
    }

    fun startProcessing() {
        require(status == PaymentStatus.PENDING) {
            "Cannot process a payment with status $status"
        }
        status = PaymentStatus.PROCESSING
        updatedAt = LocalDateTime.now()
    }

    fun isPending(): Boolean = status == PaymentStatus.PENDING
    fun isTerminal(): Boolean = status in listOf(PaymentStatus.APPROVED, PaymentStatus.FAILED)
}