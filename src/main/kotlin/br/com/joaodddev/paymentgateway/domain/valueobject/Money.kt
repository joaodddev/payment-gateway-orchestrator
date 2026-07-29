package br.com.joaodddev.paymentgateway.domain.valueobject

import java.math.BigDecimal
import java.math.RoundingMode

data class Money(
    val amount: BigDecimal,
    val currency: String = "BRL"
) {
    init {
        require(amount > BigDecimal.ZERO) { "Amount must be positive" }
        require(currency.length == 3) { "Currency code must be 3 characters" }
    }

    fun toFormattedString(): String =
        "$currency ${amount.setScale(2, RoundingMode.HALF_UP)}"

    override fun toString(): String = toFormattedString()
}