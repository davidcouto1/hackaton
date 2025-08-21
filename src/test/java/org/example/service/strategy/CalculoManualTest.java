package org.example.service.strategy;

import org.example.dto.SimulacaoResponseDTO;
import org.example.dto.SimulacaoRequestDTO;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class CalculoManualTest {
    @Test
    void testSACPrimeirasParcelas() {
        SacSimulacaoStrategy sac = new SacSimulacaoStrategy();
        SimulacaoRequestDTO.ModeloEnvelopeSimulacao env = new SimulacaoRequestDTO.ModeloEnvelopeSimulacao();
        env.setValorDesejado(30000.0);
        env.setPrazo(24);
        double taxa = 2.0; // 2% a.m.
        SimulacaoResponseDTO.ResultadoSimulacao resultado = sac.calcularParcelas(env, taxa);
        List<SimulacaoResponseDTO.Parcela> parcelas = resultado.getParcelas();
        assertEquals(24, parcelas.size());
        // 1ª parcela
        assertEquals(1250.0, parcelas.get(0).getValorAmortizacao(), 0.01);
        assertEquals(600.0, parcelas.get(0).getValorJuros(), 0.01);
        assertEquals(1850.0, parcelas.get(0).getValorPrestacao(), 0.01);
        // 2ª parcela
        assertEquals(1250.0, parcelas.get(1).getValorAmortizacao(), 0.01);
        assertEquals(575.0, parcelas.get(1).getValorJuros(), 0.01);
        assertEquals(1825.0, parcelas.get(1).getValorPrestacao(), 0.01);
    }

    @Test
    void testPricePrimeirasParcelas() {
        PriceSimulacaoStrategy price = new PriceSimulacaoStrategy();
        SimulacaoRequestDTO.ModeloEnvelopeSimulacao env = new SimulacaoRequestDTO.ModeloEnvelopeSimulacao();
        env.setValorDesejado(30000.0);
        env.setPrazo(24);
        double taxa = 2.0; // 2% a.m.
        SimulacaoResponseDTO.ResultadoSimulacao resultado = price.calcularParcelas(env, taxa);
        List<SimulacaoResponseDTO.Parcela> parcelas = resultado.getParcelas();
        assertEquals(24, parcelas.size());
        // 1ª parcela
        assertEquals(1586.13, parcelas.get(0).getValorPrestacao(), 0.01);
        assertEquals(600.0, parcelas.get(0).getValorJuros(), 0.01);
        assertEquals(986.13, parcelas.get(0).getValorAmortizacao(), 0.01);
        // 2ª parcela
        assertEquals(1586.13, parcelas.get(1).getValorPrestacao(), 0.01);
        assertEquals(580.28, parcelas.get(1).getValorJuros(), 0.01);
        assertEquals(1005.85, parcelas.get(1).getValorAmortizacao(), 0.01);
    }
}

