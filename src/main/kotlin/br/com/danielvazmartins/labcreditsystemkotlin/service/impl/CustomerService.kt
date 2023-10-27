package br.com.danielvazmartins.labcreditsystemkotlin.service.impl

import br.com.danielvazmartins.labcreditsystemkotlin.entity.Customer
import br.com.danielvazmartins.labcreditsystemkotlin.exception.BusinessException
import br.com.danielvazmartins.labcreditsystemkotlin.repository.CustomerRepository
import br.com.danielvazmartins.labcreditsystemkotlin.service.ICustomerService
import org.springframework.stereotype.Service

@Service
class CustomerService(
    private val customerRepository: CustomerRepository
): ICustomerService {

    override fun save(customer: Customer): Customer {
        return this.customerRepository.save(customer)
    }

    override fun findById(id: Long): Customer {
        return this.customerRepository.findById(id).orElseThrow{
            throw BusinessException("Id $id not found!")
        }
    }

    override fun delete(id: Long) {
        val customer: Customer = this.findById(id)
        this.customerRepository.delete(customer)
    }

}