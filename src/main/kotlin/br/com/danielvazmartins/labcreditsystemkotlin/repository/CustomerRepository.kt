package br.com.danielvazmartins.labcreditsystemkotlin.repository

import br.com.danielvazmartins.labcreditsystemkotlin.entity.Customer
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CustomerRepository: JpaRepository<Customer, Long> {
}