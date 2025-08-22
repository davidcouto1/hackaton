package org.example.service.amortizacao;

import org.example.dto.SimulacaoRequestDTO;
import org.example.dto.SimulacaoResponseDTO;
import org.example.service.strategy.PriceSimulacaoStrategy;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PriceSimulacaoStrategyTest {
    @Test
    void calcularParcelasDeveRetornarTipoPrice() {
        PriceSimulacaoStrategy strategy = new PriceSimulacaoStrategy();
        SimulacaoRequestDTO.ModeloEnvelopeSimulacao dados = new SimulacaoRequestDTO.ModeloEnvelopeSimulacao();
        dados.setValorDesejado(30000.0);
        dados.setPrazo(24);
        SimulacaoResponseDTO.ResultadoSimulacao resultado = strategy.calcularParcelas(dados, 2.0);
        assertEquals("PRICE", resultado.getTipo());
    }

    @Test
    void getTipoDeveRetornarPrice() {
        PriceSimulacaoStrategy strategy = new PriceSimulacaoStrategy();
        assertEquals("PRICE", strategy.getTipo());
    }

    @Test
    void calcularParcelasValoresReais() {
        PriceSimulacaoStrategy strategy = new PriceSimulacaoStrategy();
        SimulacaoRequestDTO.ModeloEnvelopeSimulacao dados = new SimulacaoRequestDTO.ModeloEnvelopeSimulacao();
        dados.setValorDesejado(30000.0);
        dados.setPrazo(24);
        SimulacaoResponseDTO.ResultadoSimulacao resultado = strategy.calcularParcelas(dados, 2.0);
        List<SimulacaoResponseDTO.Parcela> parcelas = resultado.getParcelas();
        assertEquals(24, parcelas.size());
        assertEquals(1586.13, parcelas.get(0).getValorPrestacao(), 0.01);
        assertEquals(600.0, parcelas.get(0).getValorJuros(), 0.01);
        assertEquals(986.13, parcelas.get(0).getValorAmortizacao(), 0.01);
        assertEquals(1586.13, parcelas.get(1).getValorPrestacao(), 0.01);
        assertEquals(580.28, parcelas.get(1).getValorJuros(), 0.01);
        assertEquals(1005.85, parcelas.get(1).getValorAmortizacao(), 0.01);
    }

    @Test
    void calcularParcelasPrazoZero() {
        PriceSimulacaoStrategy strategy = new PriceSimulacaoStrategy();
        SimulacaoRequestDTO.ModeloEnvelopeSimulacao dados = new SimulacaoRequestDTO.ModeloEnvelopeSimulacao();
        dados.setValorDesejado(10000.0);
        dados.setPrazo(0);
        SimulacaoResponseDTO.ResultadoSimulacao resultado = strategy.calcularParcelas(dados, 2.0);
        assertEquals(1, resultado.getParcelas().size());
        assertEquals(0.0, resultado.getParcelas().get(0).getValorPrestacao(), 0.01);
    }

    @Test
    void calcularParcelasValorZero() {
        PriceSimulacaoStrategy strategy = new PriceSimulacaoStrategy();
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
        PriceSimulacaoStrategy strategy = new PriceSimulacaoStrategy();
        SimulacaoRequestDTO.ModeloEnvelopeSimulacao dados = new SimulacaoRequestDTO.ModeloEnvelopeSimulacao();
        dados.setValorDesejado(1000.0);
        dados.setPrazo(1);
        SimulacaoResponseDTO.ResultadoSimulacao resultado = strategy.calcularParcelas(dados, 0.0);
        assertEquals(1, resultado.getParcelas().size());
        assertEquals(1000.0, resultado.getParcelas().get(0).getValorPrestacao(), 0.01);
    }

    @Test
    void calcularParcelasValorNegativo() {
        PriceSimulacaoStrategy strategy = new PriceSimulacaoStrategy();
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
        PriceSimulacaoStrategy strategy = new PriceSimulacaoStrategy();
        SimulacaoRequestDTO.ModeloEnvelopeSimulacao dados = new SimulacaoRequestDTO.ModeloEnvelopeSimulacao();
        dados.setValorDesejado(1000.0);
        dados.setPrazo(-5);
        SimulacaoResponseDTO.ResultadoSimulacao resultado = strategy.calcularParcelas(dados, 2.0);
        assertTrue(resultado.getParcelas().isEmpty() || resultado.getParcelas().stream().allMatch(p -> p.getValorPrestacao() == 0.0));
    }

    @Test
    void calcularParcelasPrazoMinimo() {
        PriceSimulacaoStrategy strategy = new PriceSimulacaoStrategy();
        SimulacaoRequestDTO.ModeloEnvelopeSimulacao dados = new SimulacaoRequestDTO.ModeloEnvelopeSimulacao();
        dados.setValorDesejado(1000.0);
        dados.setPrazo(1);
        SimulacaoResponseDTO.ResultadoSimulacao resultado = strategy.calcularParcelas(dados, 2.0);
        assertEquals(1, resultado.getParcelas().size());
        assertEquals(1000.0, resultado.getParcelas().get(0).getValorPrestacao(), 0.01);
    }
}
