package com.wlanboy.demo.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wlanboy.demo.model.HelloParameters;

public class HelloControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private HelloController helloController;
    
    private ObjectMapper objectMapper = new ObjectMapper();

    @Before
    public void init(){
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders
                .standaloneSetup(helloController)
                .build();
    }
    
    @Test
    public void test_get_all_success() throws Exception {
        
        mockMvc.perform(get("/hello/test"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.identifier").value(1))
                .andExpect(jsonPath("$.target").value("Hello"))
                .andExpect(jsonPath("$.status").value("test"));
    }
    
    @Test
    public void test_create_hello_success() throws Exception {
    	long id = 1;
        HelloParameters testrequest = new HelloParameters(id, "hello","ok");
        
        mockMvc.perform(
        		 
                post("/hello")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testrequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.identifier").value(id+1))
                .andExpect(jsonPath("$.target").value("hello"))
                .andExpect(jsonPath("$.status").value("ok"));                
        
    }
    
    
}
