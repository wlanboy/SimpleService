package com.wlanboy.demo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wlanboy.demo.GenesisInitializer;
import com.wlanboy.demo.model.AuditLog;
import com.wlanboy.demo.repository.AuditData;
import com.wlanboy.demo.repository.AuditRepositorySimple;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
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
        // 1. Audit erzeugen
        AuditLog log = AuditLog.builder()
                .target("system")
                .status("OK")
                .counter(1L)
                .build();

        String response = mockMvc.perform(post("/audit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(log)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        System.out.println("DB count = " + auditRepository.count());

        AuditLog saved = objectMapper.readValue(response, AuditLog.class);

        // 2. Verify aufrufen
        mockMvc.perform(get("/audit/" + saved.getIdentifier() + "/verify"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("valid")));
    }

    @Test
    void testVerifyInvalidEntryAfterManipulation() throws Exception {
        // 1. Audit erzeugen
        AuditLog log = AuditLog.builder()
                .target("system")
                .status("OK")
                .counter(1L)
                .build();

        String response = mockMvc.perform(post("/audit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(log)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        System.out.println("DB count = " + auditRepository.count());

        AuditLog saved = objectMapper.readValue(response, AuditLog.class);

        // 2. Manipulation simulieren
        AuditData data = auditRepository.findById(saved.getIdentifier()).get();
        data.setStatus("HACKED");
        auditRepository.save(data);

        // 3. Verify muss jetzt fehlschlagen
        mockMvc.perform(get("/audit/" + saved.getIdentifier() + "/verify"))
                .andExpect(status().isConflict())
                .andExpect(content().string(containsString("INVALID")));
    }

    @Test
    void testVerifyNotFound() throws Exception {
        mockMvc.perform(get("/audit/99999/verify"))
                .andExpect(status().isNotFound());
    }
    
}
