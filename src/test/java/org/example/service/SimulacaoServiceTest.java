package org.example.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.SimulacaoRequestDTO;
import org.example.dto.SimulacaoResponseDTO;
import org.example.model.Produto;
import org.example.model.Simulacao;
import org.example.repository.AuditoriaRepository;
import org.example.repository.SimulacaoRepository;
import org.example.service.strategy.SimulacaoStrategy;
import org.example.service.strategy.SimulacaoStrategyFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SimulacaoServiceTest {
    private ProdutoService produtoService;
    private SimulacaoRepository simulacaoRepository;
    private EventHubService eventHubService;
    private ObjectMapper objectMapper;
    private AuditoriaRepository auditoriaRepository;
    private EventHubQueueProducer eventHubQueueProducer;
    private SimulacaoStrategyFactory simulacaoStrategyFactory;
    private SimulacaoService simulacaoService;

    @BeforeEach
    void setup() {
        produtoService = mock(ProdutoService.class);
        simulacaoRepository = mock(SimulacaoRepository.class);
        eventHubService = mock(EventHubService.class);
        objectMapper = new ObjectMapper();
        auditoriaRepository = mock(AuditoriaRepository.class);
        eventHubQueueProducer = mock(EventHubQueueProducer.class);
        simulacaoStrategyFactory = mock(SimulacaoStrategyFactory.class);
        simulacaoService = new SimulacaoService(produtoService, simulacaoRepository, eventHubService, objectMapper, auditoriaRepository, eventHubQueueProducer, simulacaoStrategyFactory);
    }

    @Test
    void testSalvarSimulacao() {
        Simulacao sim = new Simulacao();
        when(simulacaoRepository.save(any())).thenReturn(sim);
        Simulacao result = simulacaoService.salvarSimulacao(sim);
        assertNotNull(result);
        verify(simulacaoRepository).save(sim);
        verify(auditoriaRepository).save(any());
    }

    @Test
    void testSalvarSimulacaoDeveLancarExcecaoQuandoBancoIndisponivel() {
        Simulacao sim = new Simulacao();
        when(simulacaoRepository.save(any())).thenThrow(new RuntimeException("Banco indisponível"));
        Exception ex = assertThrows(RuntimeException.class, () -> simulacaoService.salvarSimulacao(sim));
        assertTrue(ex.getMessage().contains("Banco indisponível"));
    }

    @Test
    void testListarSimulacoes() {
        when(simulacaoRepository.findAll()).thenReturn(Collections.singletonList(new Simulacao()));
        List<Simulacao> result = simulacaoService.listarSimulacoes();
        assertEquals(1, result.size());
    }

    @Test
    void testValidarDadosValidos() {
        Simulacao sim = new Simulacao();
        sim.setValorSolicitado(10000.0);
        sim.setPrazo(Integer.valueOf(12));
        Produto prod = new Produto();
        prod.setValorMinimo(BigDecimal.valueOf(1000));
        prod.setValorMaximo(BigDecimal.valueOf(20000));
        prod.setPrazoMinimo(Short.valueOf((short)6));
        prod.setPrazoMaximo(Short.valueOf((short)24));
        assertTrue(simulacaoService.validarDados(sim, prod));
    }

    @Test
    void testValidarDadosInvalidos() {
        Simulacao sim = new Simulacao();
        sim.setValorSolicitado(500.0);
        sim.setPrazo(Integer.valueOf(3));
        Produto prod = new Produto();
        prod.setValorMinimo(BigDecimal.valueOf(1000));
        prod.setValorMaximo(BigDecimal.valueOf(20000));
        prod.setPrazoMinimo(Short.valueOf((short)6));
        prod.setPrazoMaximo(Short.valueOf((short)24));
        assertFalse(simulacaoService.validarDados(sim, prod));
    }

    @Test
    void testFluxoCompletoSimulacaoDTO() {
        Produto produto = new Produto();
        produto.setNome("Produto Teste");
        produto.setTaxaJuros(BigDecimal.valueOf(2.0));
        when(produtoService.listarProdutos()).thenReturn(Collections.singletonList(produto));
        SimulacaoRequestDTO.ModeloEnvelopeSimulacao env = new SimulacaoRequestDTO.ModeloEnvelopeSimulacao();
        env.setValorDesejado(30000.0);
        env.setPrazo(Integer.valueOf(24));
        SimulacaoRequestDTO req = new SimulacaoRequestDTO();
        req.setModeloEnvelopeSimulacao(env);
        SimulacaoStrategy sacStrategy = mock(SimulacaoStrategy.class);
        SimulacaoStrategy priceStrategy = mock(SimulacaoStrategy.class);
        SimulacaoResponseDTO.ResultadoSimulacao sacResult = new SimulacaoResponseDTO.ResultadoSimulacao();
        sacResult.setTipo("SAC");
        sacResult.setParcelas(Collections.emptyList());
        SimulacaoResponseDTO.ResultadoSimulacao priceResult = new SimulacaoResponseDTO.ResultadoSimulacao();
        priceResult.setTipo("PRICE");
        priceResult.setParcelas(Collections.emptyList());
        when(simulacaoStrategyFactory.getStrategy("Sac")).thenReturn(sacStrategy);
        when(simulacaoStrategyFactory.getStrategy("Price")).thenReturn(priceStrategy);
        when(sacStrategy.calcularParcelas(any(), anyDouble())).thenReturn(sacResult);
        when(priceStrategy.calcularParcelas(any(), anyDouble())).thenReturn(priceResult);
        when(simulacaoRepository.save(any())).thenReturn(new Simulacao());
        SimulacaoResponseDTO resp = simulacaoService.fluxoCompletoSimulacaoDTO(req);
        assertNotNull(resp);
        assertEquals(2, resp.getModeloEnvelopeRetornoSimulacao().getResultadoSimulacao().size());
        assertEquals("SAC", resp.getModeloEnvelopeRetornoSimulacao().getResultadoSimulacao().get(0).getTipo());
        assertEquals("PRICE", resp.getModeloEnvelopeRetornoSimulacao().getResultadoSimulacao().get(1).getTipo());
    }

    @Test
    void testFluxoCompletoSimulacaoDTONenhumProdutoValido() {
        // Nenhum produto válido para os parâmetros informados
        when(produtoService.listarProdutos()).thenReturn(Collections.emptyList());
        SimulacaoRequestDTO.ModeloEnvelopeSimulacao env = new SimulacaoRequestDTO.ModeloEnvelopeSimulacao();
        env.setValorDesejado(9999999.0); // Valor fora de qualquer faixa
        env.setPrazo(999); // Prazo fora de qualquer faixa
        SimulacaoRequestDTO req = new SimulacaoRequestDTO();
        req.setModeloEnvelopeSimulacao(env);
        Exception ex = assertThrows(org.example.exception.BusinessException.class, () -> simulacaoService.fluxoCompletoSimulacaoDTO(req));
        assertEquals("Não há produtos disponíveis para os parâmetros informados.", ex.getMessage());
    }

    @Test
    void testEnviarParaEventHubDeveLidarComFalha() {
        doThrow(new RuntimeException("EventHub fora do ar")).when(eventHubService).enviarMensagem(anyString());
        // Simula chamada que dispara envio para EventHub
        assertDoesNotThrow(() -> simulacaoService.simularEnvioEventHub("{\"teste\":123}"));
        // Aqui pode-se verificar se o log de erro foi chamado, se houver logger mockado
    }
}
