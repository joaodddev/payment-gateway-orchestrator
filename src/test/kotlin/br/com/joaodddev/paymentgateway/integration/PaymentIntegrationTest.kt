package br.com.joaodddev.paymentgateway.integration

import br.com.joaodddev.paymentgateway.domain.entity.PaymentStatus
import br.com.joaodddev.paymentgateway.domain.repository.PaymentRepository
import br.com.joaodddev.paymentgateway.web.dto.CreatePaymentRequest
import com.fasterxml.jackson.databind.ObjectMapper
import org.awaitility.Awaitility.await
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import org.testcontainers.containers.KafkaContainer
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import org.springframework.context.annotation.Import
import java.math.BigDecimal
import java.util.UUID
import java.util.concurrent.TimeUnit

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
@Import(TestSecurityConfig::class)
class PaymentIntegrationTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Autowired
    lateinit var paymentRepository: PaymentRepository

    companion object {
        @Container
        val postgres = PostgreSQLContainer(DockerImageName.parse("postgres:16"))
            .withDatabaseName("pgwtest")
            .withUsername("pgwuser")
            .withPassword("pgwpass")

        @Container
        val kafka = KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.6.0"))

        @JvmStatic
        @DynamicPropertySource
        fun properties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgres::getJdbcUrl)
            registry.add("spring.datasource.username", postgres::getUsername)
            registry.add("spring.datasource.password", postgres::getPassword)
            registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers)
            registry.add("spring.data.redis.host") { "localhost" }
            registry.add("spring.data.redis.port") { "6380" }
        }
    }

    @Test
    fun `should create payment and process via Kafka`() {
        val idempotencyKey = UUID.randomUUID().toString()
        val request = CreatePaymentRequest(
            payerId = "payer-integration-1",
            payeeId = "payee-integration-2",
            amount = BigDecimal("200.00"),
            currency = "BRL",
            description = "Integration test payment"
        )

        mockMvc.post("/api/v1/payments") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
            header("Idempotency-Key", idempotencyKey)
            header("Authorization", "Bearer test-bypass")
        }.andExpect {
            status { isCreated() }
        }

        await()
            .atMost(15, TimeUnit.SECONDS)
            .pollInterval(500, TimeUnit.MILLISECONDS)
            .untilAsserted {
                val payment = paymentRepository.findByIdempotencyKey(idempotencyKey)
                assertNotNull(payment)
                assertEquals(PaymentStatus.APPROVED, payment!!.status)
            }
    }

    @Test
    fun `should return existing payment on duplicate idempotency key`() {
        val idempotencyKey = UUID.randomUUID().toString()
        val request = CreatePaymentRequest(
            payerId = "payer-dup-1",
            payeeId = "payee-dup-2",
            amount = BigDecimal("100.00"),
            currency = "BRL"
        )

        repeat(2) {
            mockMvc.post("/api/v1/payments") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(request)
                header("Idempotency-Key", idempotencyKey)
                header("Authorization", "Bearer test-bypass")
            }.andExpect {
                status { isCreated() }
            }
        }

        val payments = paymentRepository.findByPayerId("payer-dup-1")
        assertEquals(1, payments.size)
    }
}