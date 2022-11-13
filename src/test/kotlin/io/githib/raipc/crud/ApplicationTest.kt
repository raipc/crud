package io.githib.raipc.crud

import io.githib.raipc.crud.convertrequest.ConvertRequestDto
import io.githib.raipc.crud.convertrequest.ConvertRequestModel
import io.githib.raipc.crud.convertrequest.ConvertRequestService
import io.githib.raipc.crud.idempotency.IdempotencyService
import io.githib.raipc.crud.idempotency.IdempotencyServiceImpl
import io.githib.raipc.crud.util.ConflictingRequestException
import io.githib.raipc.crud.util.ResourceNotFoundException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.mockito.kotlin.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.*
import java.math.BigDecimal
import java.time.Instant

@SpringBootTest
@AutoConfigureMockMvc
@Import(ApplicationTest.Config::class)
class ApplicationTest : TCIntegrationTest() {
    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var convertRequestService: ConvertRequestService

    @Autowired
    lateinit var idempotencyService: IdempotencyService<ConvertRequestModel, ConvertRequestDto>

    @TestConfiguration
    class Config {
        @Bean
        fun idempotencyService(): IdempotencyService<ConvertRequestModel, ConvertRequestDto> =
            spy(IdempotencyServiceImpl())
    }

    @BeforeEach
    fun cleanup() {
        cleanupTable("convert_request")
    }

    @Test
    fun `should submit new request on each post if no Idempotence-Key specified`() {
        val requestBody = ConvertRequestModel(
            from = "ETH",
            to = "USD",
            rate = BigDecimal("1300.00"),
            quantity = BigDecimal("0.1")
        )
        mockMvc.post("/converter/requests/new") {
            contentType = MediaType.APPLICATION_JSON
            content = requestBody.toJsonString()
        }.andExpect {
            status { isOk() }
            content { json("""{"id":1,"from":"ETH","to":"USD","quantity":0.1,"rate":1300.00}""") }
        }
        mockMvc.post("/converter/requests/new") {
            contentType = MediaType.APPLICATION_JSON
            content = requestBody.toJsonString()
        }.andExpect {
            status { isOk() }
            content { json("""{"id":2,"from":"ETH","to":"USD","quantity":0.1,"rate":1300.00}""") }
        }
    }

    @Test
    fun `should submit old response on each post if same Idempotence-Key specified`() {
        val idempotencyKey = "12345678"
        val requestBody = ConvertRequestModel(
            from = "ETH",
            to = "USD",
            rate = BigDecimal("1300.00"),
            quantity = BigDecimal("0.1")
        )
        mockMvc.post("/converter/requests/new") {
            contentType = MediaType.APPLICATION_JSON
            content = requestBody.toJsonString()
            header("Idempotency-Key", idempotencyKey)
        }.andExpect {
            status { isOk() }
            content { json("""{"id":1,"from":"ETH","to":"USD","quantity":0.1,"rate":1300.00}""") }
        }
        mockMvc.post("/converter/requests/new") {
            contentType = MediaType.APPLICATION_JSON
            content = requestBody.toJsonString()
            header("Idempotency-Key", idempotencyKey)
        }.andExpect {
            status { isOk() }
            content { json("""{"id":1,"from":"ETH","to":"USD","quantity":0.1,"rate":1300.00}""") }
        }.andReturn().response.let {
            println(it.contentAsString)
        }
    }

    @Test
    fun `should respond with 409 if previous request with same key is not finished`() {
        val idempotencyKey = "respond-409"
        val model = ConvertRequestModel(
            from = "ETH",
            to = "USD",
            rate = BigDecimal("1300.00"),
            quantity = BigDecimal("0.1")
        )
        doThrow(ConflictingRequestException(idempotencyKey))
            .`when`(idempotencyService).process(eq(idempotencyKey), eq(model), any())

        mockMvc.post("/converter/requests/new") {
            contentType = MediaType.APPLICATION_JSON
            content = model.toJsonString()
            header("Idempotency-Key", idempotencyKey)
        }.andExpect {
            status { isConflict() }
        }
    }

    @Test
    fun `should respond with 422 if request changed with same Idempotence-Key specified`() {
        val idempotencyKey = "respond-422"
        mockMvc.post("/converter/requests/new") {
            contentType = MediaType.APPLICATION_JSON
            content = ConvertRequestModel(
                from = "ETH",
                to = "USD",
                rate = BigDecimal("1300.00"),
                quantity = BigDecimal("0.1")
            ).toJsonString()
            header("Idempotency-Key", idempotencyKey)
        }.andExpect {
            status { isOk() }
            content { json("""{"id":1,"from":"ETH","to":"USD","quantity":0.1,"rate":1300.00}""") }
        }
        mockMvc.post("/converter/requests/new") {
            contentType = MediaType.APPLICATION_JSON
            content = ConvertRequestModel(
                from = "ETH",
                to = "USD",
                rate = BigDecimal("1301.00"),
                quantity = BigDecimal("0.1")
            ).toJsonString()
            header("Idempotency-Key", idempotencyKey)
        }.andExpect {
            status { isUnprocessableEntity() }
        }
    }

    @Test
    fun `should fetch all requests in CSV`() {
        val expectedData = listOf(
            convertRequestService.insert(
                ConvertRequestDto(
                    id = null,
                    from = "ETH",
                    to = "USD",
                    rate = BigDecimal("1350.00"),
                    quantity = BigDecimal("0.01"),
                    createdAt = Instant.now()
                )
            ),
            convertRequestService.insert(
                ConvertRequestDto(
                    id = null,
                    from = "ETH",
                    to = "EUR",
                    rate = BigDecimal("1380.00"),
                    quantity = BigDecimal("0.15"),
                    createdAt = Instant.now()
                )
            ),
        )
        mockMvc.get("/converter/requests").andExpect {
            status { isOk() }
            content {
                contentTypeCompatibleWith("text/csv")
                string(
                    buildString {
                        append("id,from,to,quantity,rate,createdAt\n")
                        expectedData.forEach {
                            append("${it.id},${it.from},${it.to},${it.quantity},${it.rate},${it.createdAt}\n")
                        }
                    }
                )
            }
        }
    }

    @Test
    fun `should retrieve existing request by id`() {
        val request = convertRequestService.insert(
            ConvertRequestDto(
                id = null,
                from = "ETH",
                to = "EUR",
                rate = BigDecimal("1380.00"),
                quantity = BigDecimal("0.15"),
                createdAt = Instant.now()
            )
        )
        mockMvc.get("/converter/requests/${request.id}")
            .andExpect {
                status { isOk() }
                content { json(request.toJsonString()) }
            }
    }

    @ParameterizedTest
    @ValueSource(strings = ["GET", "PUT", "DELETE"])
    fun `should respond 404 if request not found`(method: String) {
        mockMvc.request(HttpMethod.valueOf(method), "/converter/requests/42") {
            if (method == "PUT") {
                contentType = MediaType.APPLICATION_JSON
                content = ConvertRequestModel(
                    from = "ETH",
                    to = "USD",
                    rate = BigDecimal("1300.00"),
                    quantity = BigDecimal("0.1")
                ).toJsonString()
            }
        }.andExpect {
            status { isNotFound() }
        }
    }

    @Test
    fun `should update existing request`() {
        val request = convertRequestService.insert(
            ConvertRequestDto(
                id = null,
                from = "ETH",
                to = "EUR",
                rate = BigDecimal("1380.00"),
                quantity = BigDecimal("0.15"),
                createdAt = Instant.now()
            )
        )
        mockMvc.put("/converter/requests/${request.id}") {
            contentType = MediaType.APPLICATION_JSON
            content = ConvertRequestModel(
                from = "ETH",
                to = "USD",
                rate = BigDecimal("1300.00"),
                quantity = BigDecimal("0.1")
            ).toJsonString()
        }.andExpect {
            status { isOk() }
            content { json("""{"from":"ETH","to":"USD","quantity":0.1,"rate":1300.00}""") }
        }
    }

    @Test
    fun `should delete existing request`() {
        val request = convertRequestService.insert(
            ConvertRequestDto(
                id = null,
                from = "ETH",
                to = "EUR",
                rate = BigDecimal("1380.00"),
                quantity = BigDecimal("0.15"),
                createdAt = Instant.now()
            )
        )
        mockMvc.delete("/converter/requests/${request.id}")
            .andExpect {
                status { isOk() }
            }

        assertThrows<ResourceNotFoundException> { convertRequestService.findById(request.id!!) }
    }
}