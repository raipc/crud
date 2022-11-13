package io.githib.raipc.crud

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Testcontainers
import java.sql.Connection

@Testcontainers
abstract class TCIntegrationTest {
    @Autowired
    private lateinit var jsonMapper: ObjectMapper

    fun Any.toJsonString() = jsonMapper.writeValueAsString(this)

    companion object {
        @JvmStatic
        val postgreSQLContainer: PostgreSQLContainer<*> = PostgreSQLContainer("postgres:15")
            .withDatabaseName("integration-tests-db")
            .withUsername("testuser")
            .withPassword("testpassword")

        private lateinit var jdbcConnection: Connection

        fun cleanupTable(tableName: String) = jdbcConnection.createStatement().use {
            it.execute("TRUNCATE $tableName RESTART IDENTITY;")
        }

        @BeforeAll
        @JvmStatic
        fun beforeAll() {
            postgreSQLContainer.start()
            jdbcConnection = postgreSQLContainer.createConnection("")
        }

        @AfterAll
        @JvmStatic
        fun afterAll() {
            jdbcConnection.close()
        }

        @JvmStatic
        @DynamicPropertySource
        fun registerDynamicProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl)
            registry.add("spring.datasource.username", postgreSQLContainer::getUsername)
            registry.add("spring.datasource.password", postgreSQLContainer::getPassword)
        }
    }
}
