package fr.lucasbmmn.overhearrserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * The main entry point for the Overhearr Server application.
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class OverhearrServerApplication {

    static void main(String[] args) {
        SpringApplication.run(OverhearrServerApplication.class, args);
    }

}
