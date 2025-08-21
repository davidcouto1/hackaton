package org.example.controller;

import org.example.dto.*;
import org.example.model.Simulacao;
import org.example.service.SimulacaoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class SimulacaoControllerUnitTest {
    @Mock
    private SimulacaoService simulacaoService;

    @InjectMocks
    private SimulacaoController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void simularSucesso() {
        SimulacaoRequestDTO request = new SimulacaoRequestDTO();
        SimulacaoResponseDTO response = new SimulacaoResponseDTO();
        when(simulacaoService.fluxoCompletoSimulacaoDTO(any())).thenReturn(response);
        ResponseEntity<SimulacaoResponseDTO> result = controller.simular(request);
        assertEquals(200, result.getStatusCodeValue());
        assertEquals(response, result.getBody());
    }

    @Test
    void simularErro() {
        SimulacaoRequestDTO request = new SimulacaoRequestDTO();
        when(simulacaoService.fluxoCompletoSimulacaoDTO(any())).thenThrow(new RuntimeException("erro"));
        ResponseEntity<SimulacaoResponseDTO> result = controller.simular(request);
        assertEquals(500, result.getStatusCodeValue());
    }

    @Test
    void listarSimulacoesSucesso() {
        PaginatedResponseDTO<SimulacaoResumoDTO> paginated = new PaginatedResponseDTO<>();
        when(simulacaoService.listarSimulacoesPaginado(anyInt(), anyInt())).thenReturn(paginated);
        ResponseEntity<PaginatedResponseDTO<SimulacaoResumoDTO>> result = controller.listarSimulacoes(1, 10);
        assertEquals(200, result.getStatusCodeValue());
        assertEquals(paginated, result.getBody());
    }

    @Test
    void listarPorProdutoEDiaSucesso() {
        List<SimulacaoResponseDTO> lista = Collections.singletonList(new SimulacaoResponseDTO());
        when(simulacaoService.listarPorProdutoEDiaDTO(anyString(), any(), any())).thenReturn(lista);
        ResponseEntity<List<SimulacaoResponseDTO>> result = controller.listarPorProdutoEDia("produto", "2023-01-01");
        assertEquals(200, result.getStatusCodeValue());
        assertEquals(lista, result.getBody());
    }

    @Test
    void listarPorProdutoEDiaErro() {
        ResponseEntity<List<SimulacaoResponseDTO>> result = controller.listarPorProdutoEDia("produto", "data-invalida");
        assertEquals(400, result.getStatusCodeValue());
    }

    @Test
    void telemetriaSucesso() {
        TelemetriaDTO telemetria = new TelemetriaDTO();
        when(simulacaoService.gerarTelemetriaDTO()).thenReturn(telemetria);
        ResponseEntity<TelemetriaDTO> result = controller.telemetria();
        assertEquals(200, result.getStatusCodeValue());
        assertEquals(telemetria, result.getBody());
    }
}
