package com.college.dashboard;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.util.Map;

/**
 * Main Entry Point for Student Attendance & Performance Dashboard Application.
 */
@SpringBootApplication
public class StudentDashboardApplication {

    private static final Logger logger = LoggerFactory.getLogger(StudentDashboardApplication.class);

    @Value("${app.datasource.url:${DB_URL:${spring.datasource.url:jdbc:mysql://localhost:3306/eventdb?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC}}}")
    private String dbUrl;

    @Value("${app.datasource.username:${DB_USERNAME:${spring.datasource.username:eventapp}}}")
    private String dbUser;

    @Value("${app.datasource.password:${DB_PASSWORD:${spring.datasource.password:EventApp@123}}}")
    private String dbPass;

    @Bean
    @Primary
    public DataSource dataSource() {
        logger.info("Initializing DataSource for configured database as user: {}", dbUser);
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(normalizeJdbcUrl(dbUrl));
        config.setUsername(dbUser);
        config.setPassword(dbPass);
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setConnectionTimeout(20000);
        return new HikariDataSource(config);
    }

    static String normalizeJdbcUrl(String url) {
        if (url.startsWith("postgres://")) {
            return "jdbc:postgresql://" + url.substring("postgres://".length());
        }
        if (url.startsWith("postgresql://")) {
            return "jdbc:" + url;
        }
        return url;
    }

    public static void main(String[] args) {
        // Guard against any inherited cloud MySQL environment variables in the parent process
        String envPass = System.getenv("SPRING_DATASOURCE_PASSWORD");
        if (envPass != null && (envPass.startsWith("AVNS_") || envPass.length() > 20)) {
            System.setProperty("spring.datasource.password", "EventApp@123");
            System.setProperty("app.datasource.password", "EventApp@123");
        }
        SpringApplication application = new SpringApplication(StudentDashboardApplication.class);
        application.setDefaultProperties(Map.of("server.port", System.getenv().getOrDefault("PORT", "8080")));
        application.run(args);
        System.out.println("==================================================================");
        System.out.println("🚀 Student Attendance & Performance Dashboard is RUNNING!");
        System.out.println("🌐 Open in your browser: http://localhost:" + System.getenv().getOrDefault("PORT", "8080"));
        System.out.println("🔐 Demo Admin Login:   admin   / admin123");
        System.out.println("🔐 Demo Teacher Login: teacher / teacher123");
        System.out.println("==================================================================");
    }
}
