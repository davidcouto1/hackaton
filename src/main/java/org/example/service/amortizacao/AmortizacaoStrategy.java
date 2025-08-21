package org.example.service.amortizacao;

import org.example.dto.SimulacaoRequestDTO;
import org.example.dto.SimulacaoResponseDTO;

public interface AmortizacaoStrategy {
    SimulacaoResponseDTO.ResultadoSimulacao calcularParcelas(SimulacaoRequestDTO.ModeloEnvelopeSimulacao dadosSimulacao, double taxaJuros);
    String getTipo();
}
