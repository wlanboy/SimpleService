package com.wlanboy.demo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wlanboy.demo.GenesisInitializer;
import com.wlanboy.demo.TestConfig;
import com.wlanboy.demo.model.AuditLog;
import com.wlanboy.demo.repository.AuditData;
import com.wlanboy.demo.repository.AuditRepositorySimple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@Import(TestConfig.class)
class AuditVerifyTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuditRepositorySimple auditRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private GenesisInitializer genesisInitializer;

    @BeforeEach
    void setup() throws Exception {
        auditRepository.deleteAll();
        genesisInitializer.createGenesisBlock(auditRepository).run(null);
    }

    @Test
    void testVerifyValidEntry() throws Exception {
        // HINZUFÜGEN: hash und previousHash für die Validierung
        AuditLog log = AuditLog.builder()
                .target("system")
                .status("OK")
                .counter(1L)
                .previousHash("GENESIS")
                .hash("dummy-hash")
                .build();

        String response = mockMvc.perform(post("/audit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(log)))
                .andExpect(status().isCreated()) // Erwartet jetzt 201 statt 400
                .andReturn()
                .getResponse()
                .getContentAsString();

        AuditLog saved = objectMapper.readValue(response, AuditLog.class);

        mockMvc.perform(get("/audit/" + saved.getIdentifier() + "/verify"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("valid")));
    }

    @Test
    void testVerifyInvalidEntryAfterManipulation() throws Exception {
        // HINZUFÜGEN: hash und previousHash für die Validierung
        AuditLog log = AuditLog.builder()
                .target("system")
                .status("OK")
                .counter(1L)
                .previousHash("GENESIS")
                .hash("dummy-hash")
                .build();

        String response = mockMvc.perform(post("/audit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(log)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        AuditLog saved = objectMapper.readValue(response, AuditLog.class);

        // Manipulation simulieren
        AuditData data = auditRepository.findById(saved.getIdentifier())
                .orElseThrow(() -> new RuntimeException("Test Data not found"));
        data.setStatus("HACKED");
        auditRepository.save(data);

        mockMvc.perform(get("/audit/" + saved.getIdentifier() + "/verify"))
                .andExpect(status().isConflict())
                .andExpect(content().string(containsString("INVALID")));
    }
}