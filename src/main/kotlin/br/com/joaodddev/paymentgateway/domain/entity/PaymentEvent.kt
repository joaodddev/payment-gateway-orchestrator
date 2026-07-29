package br.com.joaodddev.paymentgateway.domain.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "payment_events")
class PaymentEvent(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", nullable = false)
    val payment: Payment,

    @Column(name = "event_type", nullable = false, length = 50)
    val eventType: String,

    @Column(columnDefinition = "TEXT")
    val payload: String? = null,

    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
)