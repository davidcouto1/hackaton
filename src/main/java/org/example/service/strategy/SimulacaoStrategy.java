package org.example.service.strategy;

import java.util.List;
import org.example.dto.SimulacaoResponseDTO;
import org.example.model.Simulacao;
import org.example.model.Produto;

public interface SimulacaoStrategy {
    List<SimulacaoResponseDTO.Parcela> calcular(Simulacao simulacao, Produto produto);
    String getTipo();
    SimulacaoResponseDTO.ResultadoSimulacao calcularParcelas(org.example.dto.SimulacaoRequestDTO.ModeloEnvelopeSimulacao env, double taxaJuros);
}
