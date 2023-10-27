package br.com.danielvazmartins.labcreditsystemkotlin.service.impl

import br.com.danielvazmartins.labcreditsystemkotlin.entity.Credit
import br.com.danielvazmartins.labcreditsystemkotlin.exception.BusinessException
import br.com.danielvazmartins.labcreditsystemkotlin.repository.CreditRepository
import br.com.danielvazmartins.labcreditsystemkotlin.service.ICreditService
import org.springframework.stereotype.Service
import java.util.*

@Service
class CreditService(
    private val creditRepository: CreditRepository,
    private val customerService: CustomerService
): ICreditService {
    override fun save(credit: Credit): Credit {
        credit.apply {
            customer = customerService.findById(credit.customer?.id!!)
        }
        return this.creditRepository.save(credit)
    }

    override fun findAllByCustomer(customerId: Long): List<Credit> {
        return this.creditRepository.findAllByCustomer(customerId)
    }

    override fun findByCreditCode(customerId: Long, creditCode: UUID): Credit {
        val credit: Credit = this.creditRepository.findByCreditCode(creditCode)
            ?: throw BusinessException("Creditcode $creditCode not found!")
        return if (credit.customer?.id == customerId) credit else throw IllegalArgumentException("Contact the admin!")
    }
}