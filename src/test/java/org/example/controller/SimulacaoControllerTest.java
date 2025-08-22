package org.example.controller;

import org.example.config.SecurityConfig;
import org.example.dto.SimulacaoRequestDTO;
import org.example.dto.SimulacaoResponseDTO;
import org.example.exception.BusinessException;
import org.example.service.SimulacaoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WithMockUser(username = "admin", roles = {"USER"})
@WebMvcTest(SimulacaoController.class)
@Import(SecurityConfig.class)
class SimulacaoControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SimulacaoService simulacaoService;

    private SimulacaoResponseDTO responseDTO;

    @BeforeEach
    void setup() {
        org.example.filter.RateLimitFilter.resetRequestCounts();
        responseDTO = new SimulacaoResponseDTO();
        SimulacaoResponseDTO.ModeloEnvelopeRetornoSimulacao env = new SimulacaoResponseDTO.ModeloEnvelopeRetornoSimulacao();
        env.setIdSimulacao(1L);
        responseDTO.setModeloEnvelopeRetornoSimulacao(env);
    }

    @Test
    void simularDeveRetornar200ComBody() throws Exception {
        when(simulacaoService.fluxoCompletoSimulacaoDTO(any())).thenReturn(responseDTO);
        String json = "{\"modeloEnvelopeSimulacao\":{\"valorDesejado\":10000,\"prazo\":12}}";
        mockMvc.perform(post("/simulacoes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.modeloEnvelopeRetornoSimulacao.idSimulacao").value(1));
    }

    @Test
    void simularDeveRetornar400QuandoServiceLancaExcecao() throws Exception {
        when(simulacaoService.fluxoCompletoSimulacaoDTO(any())).thenThrow(new BusinessException("Erro de validação"));
        String json = "{\"modeloEnvelopeSimulacao\":{\"valorDesejado\":10000,\"prazo\":12}}";
        mockMvc.perform(post("/simulacoes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void simularDeveRetornar400ParaRequestInvalido() throws Exception {
        // Enviando JSON inválido (faltando campos obrigatórios)
        String json = "{}";
        mockMvc.perform(post("/simulacoes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void simularDeveRetornar500ParaErroInterno() throws Exception {
        when(simulacaoService.fluxoCompletoSimulacaoDTO(any())).thenThrow(new NullPointerException("Erro interno"));
        String json = "{\"modeloEnvelopeSimulacao\":{\"valorDesejado\":10000,\"prazo\":12}}";
        mockMvc.perform(post("/simulacoes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isInternalServerError());
    }
}
