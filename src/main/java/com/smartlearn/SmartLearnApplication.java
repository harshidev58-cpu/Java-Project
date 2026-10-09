package com.smartlearn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Starts the SmartLearn Spring Boot application.
 * This class demonstrates the application entry point and Spring Boot's component scanning.
 */
@SpringBootApplication
@EnableScheduling
public class SmartLearnApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartLearnApplication.class, args);
    }
}
