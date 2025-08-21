package org.example.service.amortizacao;

import org.example.dto.SimulacaoRequestDTO;
import org.example.dto.SimulacaoResponseDTO;
import org.example.service.strategy.SacSimulacaoStrategy;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SacSimulacaoStrategyTest {
    @Test
    void calcularParcelasDeveRetornarTipoSac() {
        SacSimulacaoStrategy strategy = new SacSimulacaoStrategy();
        SimulacaoRequestDTO.ModeloEnvelopeSimulacao dados = new SimulacaoRequestDTO.ModeloEnvelopeSimulacao();
        dados.setValorDesejado(30000.0);
        dados.setPrazo(24);
        SimulacaoResponseDTO.ResultadoSimulacao resultado = strategy.calcularParcelas(dados, 2.0);
        assertEquals("SAC", resultado.getTipo());
    }

    @Test
    void getTipoDeveRetornarSac() {
        SacSimulacaoStrategy strategy = new SacSimulacaoStrategy();
        assertEquals("SAC", strategy.getTipo());
    }

    @Test
    void calcularParcelasValoresReais() {
        SacSimulacaoStrategy strategy = new SacSimulacaoStrategy();
        SimulacaoRequestDTO.ModeloEnvelopeSimulacao dados = new SimulacaoRequestDTO.ModeloEnvelopeSimulacao();
        dados.setValorDesejado(30000.0);
        dados.setPrazo(24);
        SimulacaoResponseDTO.ResultadoSimulacao resultado = strategy.calcularParcelas(dados, 2.0);
        List<SimulacaoResponseDTO.Parcela> parcelas = resultado.getParcelas();
        assertEquals(24, parcelas.size());
        assertEquals(1250.0, parcelas.get(0).getValorAmortizacao(), 0.01);
        assertEquals(600.0, parcelas.get(0).getValorJuros(), 0.01);
        assertEquals(1850.0, parcelas.get(0).getValorPrestacao(), 0.01);
        assertEquals(1250.0, parcelas.get(1).getValorAmortizacao(), 0.01);
        assertEquals(575.0, parcelas.get(1).getValorJuros(), 0.01);
        assertEquals(1825.0, parcelas.get(1).getValorPrestacao(), 0.01);
    }

    @Test
    void calcularParcelasPrazoZero() {
        SacSimulacaoStrategy strategy = new SacSimulacaoStrategy();
        SimulacaoRequestDTO.ModeloEnvelopeSimulacao dados = new SimulacaoRequestDTO.ModeloEnvelopeSimulacao();
        dados.setValorDesejado(10000.0);
        dados.setPrazo(0);
        SimulacaoResponseDTO.ResultadoSimulacao resultado = strategy.calcularParcelas(dados, 2.0);
        assertTrue(resultado.getParcelas().isEmpty());
    }

    @Test
    void calcularParcelasValorZero() {
        SacSimulacaoStrategy strategy = new SacSimulacaoStrategy();
        SimulacaoRequestDTO.ModeloEnvelopeSimulacao dados = new SimulacaoRequestDTO.ModeloEnvelopeSimulacao();
        dados.setValorDesejado(0.0);
        dados.setPrazo(12);
        SimulacaoResponseDTO.ResultadoSimulacao resultado = strategy.calcularParcelas(dados, 2.0);
        for (SimulacaoResponseDTO.Parcela p : resultado.getParcelas()) {
            assertEquals(0.0, p.getValorAmortizacao(), 0.01);
            assertEquals(0.0, p.getValorJuros(), 0.01);
            assertEquals(0.0, p.getValorPrestacao(), 0.01);
        }
    }

    @Test
    void calcularParcelasTaxaZero() {
        SacSimulacaoStrategy strategy = new SacSimulacaoStrategy();
        SimulacaoRequestDTO.ModeloEnvelopeSimulacao dados = new SimulacaoRequestDTO.ModeloEnvelopeSimulacao();
        dados.setValorDesejado(12000.0);
        dados.setPrazo(12);
        SimulacaoResponseDTO.ResultadoSimulacao resultado = strategy.calcularParcelas(dados, 0.0);
        for (SimulacaoResponseDTO.Parcela p : resultado.getParcelas()) {
            assertEquals(1000.0, p.getValorAmortizacao(), 0.01);
            assertEquals(0.0, p.getValorJuros(), 0.01);
            assertEquals(1000.0, p.getValorPrestacao(), 0.01);
        }
    }

    @Test
    void calcularParcelasValorNegativo() {
        SacSimulacaoStrategy strategy = new SacSimulacaoStrategy();
        SimulacaoRequestDTO.ModeloEnvelopeSimulacao dados = new SimulacaoRequestDTO.ModeloEnvelopeSimulacao();
        dados.setValorDesejado(-1000.0);
        dados.setPrazo(12);
        SimulacaoResponseDTO.ResultadoSimulacao resultado = strategy.calcularParcelas(dados, 2.0);
        for (SimulacaoResponseDTO.Parcela p : resultado.getParcelas()) {
            assertEquals(0.0, p.getValorAmortizacao(), 0.01);
            assertEquals(0.0, p.getValorJuros(), 0.01);
            assertEquals(0.0, p.getValorPrestacao(), 0.01);
        }
    }

    @Test
    void calcularParcelasPrazoNegativo() {
        SacSimulacaoStrategy strategy = new SacSimulacaoStrategy();
        SimulacaoRequestDTO.ModeloEnvelopeSimulacao dados = new SimulacaoRequestDTO.ModeloEnvelopeSimulacao();
        dados.setValorDesejado(1000.0);
        dados.setPrazo(-5);
        SimulacaoResponseDTO.ResultadoSimulacao resultado = strategy.calcularParcelas(dados, 2.0);
        assertTrue(resultado.getParcelas().isEmpty() || resultado.getParcelas().stream().allMatch(p -> p.getValorPrestacao() == 0.0));
    }

    @Test
    void calcularParcelasPrazoMinimo() {
        SacSimulacaoStrategy strategy = new SacSimulacaoStrategy();
        SimulacaoRequestDTO.ModeloEnvelopeSimulacao dados = new SimulacaoRequestDTO.ModeloEnvelopeSimulacao();
        dados.setValorDesejado(1000.0);
        dados.setPrazo(1);
        SimulacaoResponseDTO.ResultadoSimulacao resultado = strategy.calcularParcelas(dados, 2.0);
        assertEquals(1, resultado.getParcelas().size());
        assertEquals(1000.0, resultado.getParcelas().get(0).getValorAmortizacao(), 0.01);
    }
}
