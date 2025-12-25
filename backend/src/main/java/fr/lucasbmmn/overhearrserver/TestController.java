package fr.lucasbmmn.overhearrserver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/test-db")
    public String testDatabaseConnection() {
        try {
            // execute a simple query to test DB interaction
            String result = jdbcTemplate.queryForObject("SELECT 'Hello from PostgreSQL!'", String.class);
            return "Backend says: Connection successful. Database says: " + result;
        } catch (Exception e) {
            return "Database connection failed: " + e.getMessage();
        }
    }
}
