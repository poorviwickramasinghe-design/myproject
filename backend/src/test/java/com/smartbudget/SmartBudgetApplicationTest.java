package com.smartbudget;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Application context load test.
 * Verifies that the Spring Boot context starts without errors.
 */
@SpringBootTest
@ActiveProfiles("test")
class SmartBudgetApplicationTest {

    @Test
    void contextLoads() {
        // If this test passes, the application context wired correctly
    }
}
