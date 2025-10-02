package com.devstoblu.banking_system.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class HealthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void pingDeveTornarStatus200() throws Exception {
        mockMvc.perform(get("/api/ping"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("Sistema funcionando"))
                .andExpect(jsonPath("$.message").value("Pong! API está respondendo corretamente"))
                .andExpect(jsonPath("$.version").value("1.0.0"));
    }

    @Test
    void healthCheckDeveTornarStatus200() throws Exception {
        mockMvc.perform(get("/api/health-check"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.application").value("Banking System"))
                .andExpect(jsonPath("$.database").value("Connected"))
                .andExpect(jsonPath("$.server").value("Running"));
    }

    @Test
    void statusDeveTornarStatus200() throws Exception {
        mockMvc.perform(get("/api/status"))
                .andExpect(status().isOk())
                .andExpect(content().string("Sistema funcionando - Status 200 OK"));
    }
}

