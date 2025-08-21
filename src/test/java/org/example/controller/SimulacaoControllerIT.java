package org.example.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SimulacaoControllerIT {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void acessoProtegidoDeveRetornar401SemAutenticacao() throws Exception {
        mockMvc.perform(get("/simulacoes"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void acessoProtegidoDeveRetornar200ComAutenticacao() throws Exception {
        mockMvc.perform(get("/simulacoes")
                .header("Authorization", "Basic " + java.util.Base64.getEncoder().encodeToString("admin:admin123".getBytes()))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
