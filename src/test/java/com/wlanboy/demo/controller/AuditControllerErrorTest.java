package com.wlanboy.demo.controller;

import com.wlanboy.demo.GenesisInitializer;
import com.wlanboy.demo.repository.AuditRepositorySimple;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
class AuditControllerErrorTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuditRepositorySimple auditRepository;

    @Autowired
    private GenesisInitializer genesisInitializer;

    @BeforeEach
    void setup() throws Exception {
        auditRepository.deleteAll();
        genesisInitializer.createGenesisBlock(auditRepository).run(null);
    }

    // ❌ Fehlerfall 1: GET /audit/{id} → ID existiert nicht
    @Test
    void testGetById_NotFound() throws Exception {
        mockMvc.perform(get("/audit/99999"))
                .andExpect(status().isNotFound());
    }

    // ❌ Fehlerfall 2: POST /audit → ungültiger Body (fehlende Felder)
    @Test
    void testCreateAudit_InvalidBody() throws Exception {
        // Leerer JSON-Body
        String invalidJson = "{}";

        mockMvc.perform(post("/audit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    // ❌ Fehlerfall 3: POST /audit → komplett ungültiges JSON
    @Test
    void testCreateAudit_MalformedJson() throws Exception {
        String malformedJson = "{ target: 'abc' "; // absichtlich kaputt

        mockMvc.perform(post("/audit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(malformedJson))
                .andExpect(status().isBadRequest());
    }

    // ❌ Fehlerfall 4: GET /audit?page=abc → ungültiger Query-Parameter
    @Test
    void testGetAll_InvalidPageParameter() throws Exception {
        mockMvc.perform(get("/audit?page=abc&size=10"))
                .andExpect(status().isBadRequest());
    }

    // ❌ Fehlerfall 5: GET /audit/search → target fehlt
    @Test
    void testSearchByTarget_MissingParameter() throws Exception {
        mockMvc.perform(get("/audit/search"))
                .andExpect(status().isBadRequest());
    }

    // ❌ Fehlerfall 6: POST /audit → null statt JSON
    @Test
    void testCreateAudit_NullBody() throws Exception {
        mockMvc.perform(post("/audit")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
