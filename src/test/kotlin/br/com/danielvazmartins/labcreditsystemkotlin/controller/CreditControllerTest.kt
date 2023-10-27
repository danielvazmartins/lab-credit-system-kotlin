package br.com.danielvazmartins.labcreditsystemkotlin.controller

import br.com.danielvazmartins.labcreditsystemkotlin.dto.CreditDTO
import br.com.danielvazmartins.labcreditsystemkotlin.dto.CustomerDTO
import br.com.danielvazmartins.labcreditsystemkotlin.entity.Address
import br.com.danielvazmartins.labcreditsystemkotlin.entity.Credit
import br.com.danielvazmartins.labcreditsystemkotlin.entity.Customer
import br.com.danielvazmartins.labcreditsystemkotlin.repository.CreditRepository
import br.com.danielvazmartins.labcreditsystemkotlin.repository.CustomerRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.hamcrest.Matchers
import org.hamcrest.collection.IsCollectionWithSize
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@ContextConfiguration
class CreditControllerTest {
    @Autowired lateinit var creditRepository: CreditRepository
    @Autowired lateinit var customerRepository: CustomerRepository
    @Autowired lateinit var mockMvc: MockMvc
    @Autowired lateinit var objectMapper: ObjectMapper

    companion object {
        const val URL:String = "/api/credits"
    }

    @BeforeEach
    fun setup() {
        creditRepository.deleteAll()
        customerRepository.deleteAll()
    }

    @AfterEach
    fun tearDown() {
        creditRepository.deleteAll()
        customerRepository.deleteAll()
    }

    @Test
    fun `should create a credit and return 201 status`() {
        //given
        val customer: Customer = customerRepository.save(buildCustomer())
        val creditDTO: CreditDTO = buildCreditDTO(customerId = customer.id!!)
        val creditDTOStr: String = objectMapper.writeValueAsString(creditDTO)
        //when
        //then
        mockMvc
            .perform(MockMvcRequestBuilders.post(URL).contentType(MediaType.APPLICATION_JSON).content(creditDTOStr))
            .andExpect(MockMvcResultMatchers.status().isCreated)
            .andExpect(MockMvcResultMatchers.jsonPath("$.creditValue").value(1000.0))
            .andExpect(MockMvcResultMatchers.jsonPath("$.numberOfInstallments").value(12))
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should find all credits by customer id and return 200 status`() {
        //given
        val customer: Customer = customerRepository.save(buildCustomer())
        creditRepository.save(buildCreditDTO(customerId = customer.id!!).toEntity())
        creditRepository.save(buildCreditDTO(customerId = customer.id!!).toEntity())
        //when
        //then
        mockMvc
            .perform(MockMvcRequestBuilders.get("$URL?customerId=${customer.id}").accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect((MockMvcResultMatchers.jsonPath("$").isArray))
            .andExpect((MockMvcResultMatchers.jsonPath("$").isNotEmpty))
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should return a empty array by invalid customer id and return 200 status`() {
        //given
        val fakeCustomerId: Long = Random().nextLong()
        //when
        //then
        mockMvc
            .perform(MockMvcRequestBuilders.get("$URL?customerId=${fakeCustomerId}").accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$").isArray)
            .andExpect(MockMvcResultMatchers.jsonPath("$").isEmpty)
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should find a credit by creditCode and customerId and return 200 status `() {
        //given
        val customer: Customer = customerRepository.save(buildCustomer())
        val credit1: Credit = creditRepository.save(buildCreditDTO(customerId = customer.id!!).toEntity())
        //when
        //then
        mockMvc
            .perform(MockMvcRequestBuilders.get("$URL/${credit1.creditCode}?customerId=${customer.id}").accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect((MockMvcResultMatchers.jsonPath("$.creditValue").value(1000.0)))
            .andExpect((MockMvcResultMatchers.jsonPath("$.numberOfInstallments").value(12)))
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should not find a credit by invalid creditCode and customerId and return 400 status `() {
        //given
        val fakeCreditCode: UUID = UUID.randomUUID()
        val fakeCustomerId: Long = Random().nextLong()
        //when
        //then
        mockMvc
            .perform(MockMvcRequestBuilders.get("$URL/${fakeCreditCode}?customerId=${fakeCustomerId}").accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Bad Request. See the documentation!"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))
            .andExpect(MockMvcResultMatchers.jsonPath("$.exception").value("class br.com.danielvazmartins.labcreditsystemkotlin.exception.BusinessException"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").isNotEmpty)
            .andDo(MockMvcResultHandlers.print())
    }

    private fun buildCreditDTO(
        creditValue: BigDecimal = BigDecimal.valueOf(1000.0),
        dayFirstInstallment: LocalDate = LocalDate.now().plusMonths(1),
        numberOfInstallments: Int = 12,
        customerId: Long = 1L
    ) = CreditDTO(
        creditValue = creditValue,
        dayFirstInstallment = dayFirstInstallment,
        numberOfInstallments = numberOfInstallments,
        customerId = customerId
    )

    private fun buildCustomer(
        firstName: String = "Daniel",
        lastName: String = "Martins",
        cpf: String = "333.333.333-22",
        email: String = "daniel@gmail.com",
        password: String = "123456",
        zipCode: String = "03333222",
        street: String = "Av. sem Nome",
        income: BigDecimal = BigDecimal.valueOf(1000.0),
    ) = Customer(
        firstName = firstName,
        lastName = lastName,
        cpf = cpf,
        email = email,
        password = password,
        address = Address(
            zipCode = zipCode,
            street = street
        ),
        income = income
    )
}