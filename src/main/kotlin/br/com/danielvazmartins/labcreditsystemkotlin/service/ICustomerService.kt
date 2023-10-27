package br.com.danielvazmartins.labcreditsystemkotlin.service

import br.com.danielvazmartins.labcreditsystemkotlin.entity.Customer

interface ICustomerService {
    fun save(customer: Customer): Customer

    fun findById(id: Long): Customer

    fun delete(id: Long)
}