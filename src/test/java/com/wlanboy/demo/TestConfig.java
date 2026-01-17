package com.wlanboy.demo;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@TestConfiguration 
public class TestConfig {
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
            // Registriert Unterstützung für LocalDateTime, OffsetDateTime etc.
            .registerModule(new JavaTimeModule())
            // Optional: Schreibt Daten als ISO-Strings statt als Timestamps
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }
}