package br.com.danielvazmartins.labcreditsystemkotlin.service

import br.com.danielvazmartins.labcreditsystemkotlin.entity.Credit
import java.util.UUID

interface ICreditService {
    fun save(credit: Credit): Credit

    fun findAllByCustomer(customerId: Long): List<Credit>

    fun findByCreditCode(customerId: Long, creditCode: UUID): Credit
}