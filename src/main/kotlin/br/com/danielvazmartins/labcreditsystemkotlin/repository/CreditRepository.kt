package br.com.danielvazmartins.labcreditsystemkotlin.repository

import br.com.danielvazmartins.labcreditsystemkotlin.entity.Credit
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CreditRepository: JpaRepository<Credit, Long> {
}