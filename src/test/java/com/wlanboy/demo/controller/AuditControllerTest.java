package com.wlanboy.demo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wlanboy.demo.GenesisInitializer;
import com.wlanboy.demo.model.AuditLog;
import com.wlanboy.demo.repository.AuditRepositorySimple;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
class AuditControllerTest {

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
    void testCreateAudit() throws Exception {
        AuditLog log = AuditLog.builder()
                .target("system")
                .status("OK")
                .counter(1L)
                .build();

        mockMvc.perform(post("/audit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(log)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.identifier", notNullValue()))
                .andExpect(jsonPath("$.target", is("system")))
                .andExpect(jsonPath("$.status", is("OK")));
    }

    @Test
    void testGetAuditById() throws Exception {
        // Erst einen AuditLog anlegen
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

        AuditLog saved = objectMapper.readValue(response, AuditLog.class);

        mockMvc.perform(get("/audit/" + saved.getIdentifier()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.identifier", is(saved.getIdentifier().intValue())))
                .andExpect(jsonPath("$.target", is("system")))
                .andExpect(jsonPath("$.status", is("OK")));
    }

    @Test
    void testGetAllAudits() throws Exception {
        // Zwei Logs anlegen
        for (int i = 0; i < 2; i++) {
            AuditLog log = AuditLog.builder()
                    .target("system")
                    .status("OK")
                    .counter((long) i)
                    .build();

            mockMvc.perform(post("/audit")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(log)))
                    .andExpect(status().isCreated());
        }

        mockMvc.perform(get("/audit?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(3)))
                .andExpect(jsonPath("$.content[0].target", is("GENESIS")));
    }

    @Test
    void testSearchByTarget() throws Exception {
        // Zwei unterschiedliche Targets
        AuditLog log1 = AuditLog.builder()
                .target("system")
                .status("OK")
                .counter(1L)
                .build();

        AuditLog log2 = AuditLog.builder()
                .target("auth")
                .status("FAIL")
                .counter(2L)
                .build();

        mockMvc.perform(post("/audit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(log1)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/audit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(log2)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/audit/search?target=system&page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].target", is("system")));
    }
}
