package br.com.danielvazmartins.labcreditsystemkotlin.dto

import br.com.danielvazmartins.labcreditsystemkotlin.entity.Credit
import br.com.danielvazmartins.labcreditsystemkotlin.entity.Customer
import jakarta.validation.constraints.Future
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal
import java.time.LocalDate

data class CreditDTO(
    @field:NotNull(message = "Invalid Input!")
    val creditValue: BigDecimal,

    @field:Future
    val dayFirstInstallment: LocalDate,

    @field:Min(value = 1)
    @field:Max(value = 48)
    val numberOfInstallments: Int,

    @field:NotNull(message = "Invalid Input!")
    val customerId: Long
) {
    fun toEntity(): Credit = Credit(
        creditValue = this.creditValue,
        dayFirstInstallment = this.dayFirstInstallment,
        numberOfInstallments = this.numberOfInstallments,
        customer = Customer(id = this.customerId)
    )
}
