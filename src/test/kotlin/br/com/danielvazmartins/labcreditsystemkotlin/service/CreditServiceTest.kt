package br.com.danielvazmartins.labcreditsystemkotlin.service

import br.com.danielvazmartins.labcreditsystemkotlin.entity.Credit
import br.com.danielvazmartins.labcreditsystemkotlin.entity.Customer
import br.com.danielvazmartins.labcreditsystemkotlin.exception.BusinessException
import br.com.danielvazmartins.labcreditsystemkotlin.repository.CreditRepository
import br.com.danielvazmartins.labcreditsystemkotlin.service.impl.CreditService
import br.com.danielvazmartins.labcreditsystemkotlin.service.impl.CustomerService
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.ActiveProfiles
import java.math.BigDecimal
import java.time.LocalDate
import java.util.Random
import java.util.UUID

@ActiveProfiles("test")
@ExtendWith(MockKExtension::class)
class CreditServiceTest {
    @MockK lateinit var creditRepository: CreditRepository
    @MockK lateinit var customerService: CustomerService
    @InjectMockKs lateinit var creditService: CreditService

    @Test
    fun `should create a credit`() {
        //given
        val fakeCredit: Credit = buildCredit()
        every { customerService.findById(fakeCredit.customer?.id!!) } returns Customer(id = fakeCredit.customer?.id!!)
        every { creditRepository.save(any()) } returns fakeCredit
        //when
        val actual: Credit = creditService.save(fakeCredit)
        //then
        Assertions.assertThat(actual).isNotNull
        Assertions.assertThat(actual).isSameAs(fakeCredit)
        verify( exactly = 1 ) { creditRepository.save(any()) }
    }

    @Test
    fun `should find all credits by customer id`() {
        //given
        val fakeCustomerId: Long = 1L
        var credits: List<Credit> = mutableListOf(buildCredit())
        every { creditRepository.findAllByCustomer(fakeCustomerId) } returns credits
        //when
        val actual: List<Credit> = creditService.findAllByCustomer(fakeCustomerId)
        //then
        Assertions.assertThat(actual).isNotNull
        Assertions.assertThat(actual).isSameAs(credits)
        verify( exactly = 1 ) { creditService.findAllByCustomer(fakeCustomerId) }
    }

    @Test
    fun `should find a credit by creditCode`() {
        //given
        val fakeCustomerId: Long = Random().nextLong()
        val fakeCredit: Credit = buildCredit(customerId = fakeCustomerId)
        every { creditRepository.findByCreditCode(fakeCredit.creditCode) } returns fakeCredit
        //when
        val actual = creditService.findByCreditCode(fakeCustomerId, fakeCredit.creditCode)
        //then
        Assertions.assertThat(actual).isNotNull
        Assertions.assertThat(actual).isSameAs(fakeCredit)
    }

    @Test
    fun `should not found a credit and thrown a BusnessException`() {
        //given
        val fakeCustomerId: Long = Random().nextLong()
        val fakeCredit: Credit = buildCredit(customerId = fakeCustomerId)
        every { creditRepository.findByCreditCode(fakeCredit.creditCode) } returns null
        //when
        //then
        Assertions.assertThatExceptionOfType(BusinessException::class.java)
            .isThrownBy { creditService.findByCreditCode(fakeCustomerId, fakeCredit.creditCode) }
            .withMessage("Creditcode ${fakeCredit.creditCode} not found!")
    }

    @Test
    fun `should not found a credit with the same customer id and thrown a IlleagalException`() {
        //given
        val fakeCustomerId: Long = Random().nextLong()
        val fakeCredit: Credit = buildCredit()
        every { creditRepository.findByCreditCode(fakeCredit.creditCode) } returns fakeCredit
        //when
        //then
        Assertions.assertThatExceptionOfType(IllegalArgumentException::class.java)
            .isThrownBy { creditService.findByCreditCode(fakeCustomerId, fakeCredit.creditCode) }
            .withMessage("Contact the admin!")
    }

    private fun buildCredit(
        creditCode: UUID = UUID.randomUUID(),
        creditValue: BigDecimal = BigDecimal.valueOf(1000.0),
        dayFirstOfInstallment: LocalDate = LocalDate.now(),
        numberOfInstallments: Int = 12,
        customerId: Long = 1L
    ) = Credit(
        creditCode = creditCode,
        creditValue = creditValue,
        dayFirstInstallment = dayFirstOfInstallment,
        numberOfInstallments = numberOfInstallments,
        customer = Customer(
            id = customerId
        )
    )
}