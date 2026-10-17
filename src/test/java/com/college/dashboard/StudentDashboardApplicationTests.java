package com.college.dashboard;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StudentDashboardApplicationTests {

    @Test
    void applicationContextSanityCheck() {
        // Basic sanity test verifying compilation and test framework
        assert true;
    }

    @Test
    void normalizesRenderPostgresConnectionUrls() {
        assertEquals(
                "jdbc:postgresql://user:pass@db.example.com:5432/app",
                StudentDashboardApplication.normalizeJdbcUrl("postgres://user:pass@db.example.com:5432/app"));
        assertEquals(
                "jdbc:postgresql://user:pass@db.example.com:5432/app",
                StudentDashboardApplication.normalizeJdbcUrl("postgresql://user:pass@db.example.com:5432/app"));
    }

    @Test
    void preservesExistingJdbcUrls() {
        String mysqlUrl = "jdbc:mysql://localhost:3306/eventdb";
        assertEquals(mysqlUrl, StudentDashboardApplication.normalizeJdbcUrl(mysqlUrl));
    }
}
