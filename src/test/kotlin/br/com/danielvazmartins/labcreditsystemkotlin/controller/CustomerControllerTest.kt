package br.com.danielvazmartins.labcreditsystemkotlin.controller

import br.com.danielvazmartins.labcreditsystemkotlin.dto.CustomerDTO
import br.com.danielvazmartins.labcreditsystemkotlin.dto.CustomerUpdateDTO
import br.com.danielvazmartins.labcreditsystemkotlin.entity.Address
import br.com.danielvazmartins.labcreditsystemkotlin.entity.Customer
import br.com.danielvazmartins.labcreditsystemkotlin.repository.CustomerRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
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
import java.util.Random

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@ContextConfiguration
class CustomerControllerTest {
    @Autowired lateinit var customerRepository: CustomerRepository
    @Autowired lateinit var mockMvc: MockMvc
    @Autowired lateinit var objectMapper: ObjectMapper

    companion object {
        const val URL:String = "/api/customers"
    }

    @BeforeEach
    fun setup() {
        customerRepository.deleteAll()
    }

    @AfterEach
    fun tearDown() {
        customerRepository.deleteAll()
    }

    @Test
    fun `should create a customer and return 201 status`() {
        //given
        val customerDTO: CustomerDTO = buildCustomerDTO()
        val customerDTOStr: String = objectMapper.writeValueAsString(customerDTO)
        //when
        //then
        mockMvc
            .perform(MockMvcRequestBuilders.post(URL).contentType(MediaType.APPLICATION_JSON).content(customerDTOStr))
            .andExpect(MockMvcResultMatchers.status().isCreated)
            .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("Daniel"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value("Martins"))
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should not save a customer with same cpf and return 409 status`() {
        //given
        customerRepository.save(buildCustomerDTO().toEntity())
        val customerDTO: CustomerDTO = buildCustomerDTO()
        val customerDTOStr: String = objectMapper.writeValueAsString(customerDTO)
        //when
        //then
        mockMvc
            .perform(MockMvcRequestBuilders.post(URL).contentType(MediaType.APPLICATION_JSON).content(customerDTOStr))
            .andExpect(MockMvcResultMatchers.status().isConflict)
            .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Bad Request. See the documentation!"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(409))
            .andExpect(MockMvcResultMatchers.jsonPath("$.exception").value("class org.springframework.dao.DataIntegrityViolationException"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").isNotEmpty)
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should not save a customer with firstName empty and return 400 status`() {
        //given
        val customerDTO: CustomerDTO = buildCustomerDTO(firstName = "")
        val customerDTOStr: String = objectMapper.writeValueAsString(customerDTO)
        //when
        //then
        mockMvc
            .perform(MockMvcRequestBuilders.post(URL).contentType(MediaType.APPLICATION_JSON).content(customerDTOStr))
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Bad Request. See the documentation!"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))
            .andExpect(MockMvcResultMatchers.jsonPath("$.exception").value("class org.springframework.web.bind.MethodArgumentNotValidException"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").isNotEmpty)
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should find customer by id and return 200 status`() {
        //given
        val customer: Customer = customerRepository.save(buildCustomerDTO().toEntity())
        //when
        //then
        mockMvc
            .perform(MockMvcRequestBuilders.get("$URL/${customer.id}").accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("Daniel"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value("Martins"))
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should not find a customer with invalid id and return 400 status`() {
        //given
        val invalidId: Long = 2L
        //when
        //then
        mockMvc
            .perform(MockMvcRequestBuilders.get("$URL/${invalidId}").accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Bad Request. See the documentation!"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))
            .andExpect(MockMvcResultMatchers.jsonPath("$.exception").value("class br.com.danielvazmartins.labcreditsystemkotlin.exception.BusinessException"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").isNotEmpty)
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should delete by customer id`() {
        //given
        val customer: Customer = customerRepository.save(buildCustomerDTO().toEntity())
        //when
        //then
        mockMvc
            .perform(MockMvcRequestBuilders.delete("$URL/${customer.id}").accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isNoContent)
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should not delete a custumer with invalid id and return 400 status`() {
        //given
        val invalidId: Long = Random().nextLong()
        //when
        //then
        mockMvc
            .perform(MockMvcRequestBuilders.delete("$URL/${invalidId}").accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Bad Request. See the documentation!"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))
            .andExpect(MockMvcResultMatchers.jsonPath("$.exception").value("class br.com.danielvazmartins.labcreditsystemkotlin.exception.BusinessException"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").isNotEmpty)
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should update a customer and return 200 status`() {
        //given
        val customer: Customer = customerRepository.save(buildCustomerDTO().toEntity())
        val customerUpdateDTO: CustomerUpdateDTO = buildCustomerUpdateDTO()
        val customerUpdateDTOStr: String = objectMapper.writeValueAsString(customerUpdateDTO)
        //when
        //then
        mockMvc
            .perform(MockMvcRequestBuilders.patch("$URL?customerId=${customer.id}").contentType(MediaType.APPLICATION_JSON).content(customerUpdateDTOStr))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value("Vaz Villalobos Martins"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.zipCode").value("04444556"))
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should not update a customer with invalid id and return 400 status`() {
        //given
        val invalidId: Long = Random().nextLong()
        val customerUpdateDTO: CustomerUpdateDTO = buildCustomerUpdateDTO()
        val customerUpdateDTOStr: String = objectMapper.writeValueAsString(customerUpdateDTO)
        //when
        //then
        mockMvc
            .perform(MockMvcRequestBuilders.patch("$URL?customerId=${invalidId}").contentType(MediaType.APPLICATION_JSON).content(customerUpdateDTOStr))
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Bad Request. See the documentation!"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))
            .andExpect(MockMvcResultMatchers.jsonPath("$.exception").value("class br.com.danielvazmartins.labcreditsystemkotlin.exception.BusinessException"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").isNotEmpty)
            .andDo(MockMvcResultHandlers.print())
    }

    private fun buildCustomerDTO(
        firstName: String = "Daniel",
        lastName: String = "Martins",
        cpf: String = "662.815.870-57",
        income: BigDecimal = BigDecimal.valueOf(1000.0),
        email: String = "daniel@gmail.com",
        password: String = "123456",
        zipCode: String = "03333222",
        street: String = "Av. sem Nome"
    ) = CustomerDTO(
        firstName = firstName,
        lastName = lastName,
        cpf = cpf,
        income = income,
        email = email,
        password = password,
        zipCode = zipCode,
        street = street
    )

    private fun buildCustomerUpdateDTO(
        firstName: String = "Daniel",
        lastName: String = "Vaz Villalobos Martins",
        income: BigDecimal = BigDecimal.valueOf(1500.0),
        zipCode: String = "04444556",
        street: String = "Av. com Nome"
    ) = CustomerUpdateDTO(
        firstName = firstName,
        lastName = lastName,
        income = income,
        zipCode = zipCode,
        street = street
    )
}