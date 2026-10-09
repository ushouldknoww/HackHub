package it.unicam.hackhub;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest
@ActiveProfiles("postgres")
class PostgresSchemaIntegrationTest {
    @Container
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("hackhub_test")
            .withUsername("hackhub_test")
            .withPassword("test-password");

    @DynamicPropertySource
    static void postgresProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.flyway.enabled", () -> true);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "validate");
        registry.add("hackhub.jwt.secret", () -> "integration-test-secret-with-at-least-32-characters");
        registry.add("hackhub.staff-registration-key", () -> "integration-staff-provisioning-secret");
    }

    @Test
    void flywayCreatesTheCoreTables(@Autowired JdbcTemplate jdbcTemplate) {
        assertEquals(0L, jdbcTemplate.queryForObject("select count(*) from auth_accounts", Long.class));
        assertEquals(0L, jdbcTemplate.queryForObject("select count(*) from audit_events", Long.class));
        assertEquals(0L, jdbcTemplate.queryForObject("select count(*) from hackathons", Long.class));
    }
}
