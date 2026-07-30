package br.com.joaodddev.paymentgateway.web.controller

import br.com.joaodddev.paymentgateway.application.usecase.CreatePaymentUseCase
import br.com.joaodddev.paymentgateway.application.usecase.GetPaymentUseCase
import br.com.joaodddev.paymentgateway.domain.entity.PaymentStatus
import br.com.joaodddev.paymentgateway.web.dto.CreatePaymentRequest
import br.com.joaodddev.paymentgateway.web.dto.PaymentResponse
import br.com.joaodddev.paymentgateway.web.dto.PaymentStatusResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/payments")
@Tag(name = "Payments", description = "Payment orchestration endpoints")
@SecurityRequirement(name = "Bearer Authentication")
class PaymentController(
    private val createPaymentUseCase: CreatePaymentUseCase,
    private val getPaymentUseCase: GetPaymentUseCase
) {

    @PostMapping
    @Operation(summary = "Create a new payment", description = "Idempotent — same Idempotency-Key returns the existing payment")
    fun create(
        @RequestBody @Valid request: CreatePaymentRequest,
        @RequestHeader("Idempotency-Key")
        @Parameter(description = "Unique key to prevent duplicate payments", required = true)
        idempotencyKey: String
    ): ResponseEntity<PaymentResponse> =
        ResponseEntity
            .status(HttpStatus.CREATED)
            .body(createPaymentUseCase.execute(request, idempotencyKey))

    @GetMapping
    @Operation(summary = "List all payments")
    fun findAll(): ResponseEntity<List<PaymentResponse>> =
        ResponseEntity.ok(getPaymentUseCase.findAll())

    @GetMapping("/{id}")
    @Operation(summary = "Find payment by ID")
    fun findById(@PathVariable id: Long): ResponseEntity<PaymentResponse> =
        ResponseEntity.ok(getPaymentUseCase.findById(id))

    @GetMapping("/{id}/status")
    @Operation(summary = "Get payment status")
    fun getStatus(@PathVariable id: Long): ResponseEntity<PaymentStatusResponse> =
        ResponseEntity.ok(getPaymentUseCase.getStatus(id))

    @GetMapping("/payer/{payerId}")
    @Operation(summary = "List payments by payer")
    fun findByPayer(@PathVariable payerId: String): ResponseEntity<List<PaymentResponse>> =
        ResponseEntity.ok(getPaymentUseCase.findByPayerId(payerId))

    @GetMapping("/status/{status}")
    @Operation(summary = "List payments by status")
    fun findByStatus(@PathVariable status: PaymentStatus): ResponseEntity<List<PaymentResponse>> =
        ResponseEntity.ok(getPaymentUseCase.findByStatus(status))
}