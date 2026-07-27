package br.com.joaodddev.paymentgateway

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class PaymentGatewayOrchestratorApplication

fun main(args: Array<String>) {
	runApplication<PaymentGatewayOrchestratorApplication>(*args)
}
