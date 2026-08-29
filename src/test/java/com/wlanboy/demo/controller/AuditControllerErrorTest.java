package com.wlanboy.demo.controller;

import com.wlanboy.demo.GenesisInitializer;
import com.wlanboy.demo.TestConfig;
import com.wlanboy.demo.repository.AuditRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
// WICHTIG: Neuer Package-Pfad für die modulare Test-Struktur in 4.0.1
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
@Import(TestConfig.class)
class AuditControllerErrorTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuditRepository auditRepository;

    @Autowired
    private GenesisInitializer genesisInitializer;

    @BeforeEach
    void setup() throws Exception {
        auditRepository.deleteAll();
        // Manuelles Triggern des Initializers für Spring 4.0.1 Standards
        genesisInitializer.createGenesisBlock(auditRepository).run(null);
    }

    @Test
    void testGetById_NotFound() throws Exception {
        mockMvc.perform(get("/audit/99999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateAudit_InvalidBody() throws Exception {
        String invalidJson = "{}";

        mockMvc.perform(post("/audit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateAudit_MalformedJson() throws Exception {
        String malformedJson = "{ target: 'abc' "; 

        mockMvc.perform(post("/audit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(malformedJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetAll_InvalidPageParameter() throws Exception {
        mockMvc.perform(get("/audit?page=abc&size=10"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testSearchByTarget_MissingParameter() throws Exception {
        mockMvc.perform(get("/audit/search"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateAudit_NullBody() throws Exception {
        mockMvc.perform(post("/audit")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}