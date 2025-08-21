package org.example.service.strategy;

import org.example.model.Simulacao;
import org.example.model.Produto;
import java.util.ArrayList;
import java.util.List;
import org.example.dto.SimulacaoResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class SacSimulacaoStrategy implements SimulacaoStrategy {
    @Override
    public List<SimulacaoResponseDTO.Parcela> calcular(Simulacao simulacao, Produto produto) {
        double valor = simulacao.getValorSolicitado();
        int prazo = simulacao.getPrazo();
        if (valor <= 0 || prazo <= 0) {
            return gerarParcelasZeradas(prazo);
        }
        double amortizacao = valor / prazo;
        double saldoDevedor = valor;
        List<SimulacaoResponseDTO.Parcela> parcelas = new ArrayList<>();
        for (int i = 1; i <= prazo; i++) {
            double juros = saldoDevedor * (simulacao.getTaxaJuros() / 100.0);
            double valorPrestacao = amortizacao + juros;
            SimulacaoResponseDTO.Parcela parcela = new SimulacaoResponseDTO.Parcela();
            parcela.setNumero(i);
            parcela.setValorAmortizacao(round(amortizacao));
            parcela.setValorJuros(round(juros));
            parcela.setValorPrestacao(round(valorPrestacao));
            parcelas.add(parcela);
            saldoDevedor -= amortizacao;
        }
        return parcelas;
    }

    private List<SimulacaoResponseDTO.Parcela> gerarParcelasZeradas(int prazo) {
        List<SimulacaoResponseDTO.Parcela> parcelas = new ArrayList<>();
        for (int n = 1; n <= Math.max(1, prazo); n++) {
            SimulacaoResponseDTO.Parcela parcela = new SimulacaoResponseDTO.Parcela();
            parcela.setNumero(n);
            parcela.setValorAmortizacao(0.0);
            parcela.setValorJuros(0.0);
            parcela.setValorPrestacao(0.0);
            parcelas.add(parcela);
        }
        return parcelas;
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    @Override
    public String getTipo() {
        return "SAC";
    }

    @Override
    public SimulacaoResponseDTO.ResultadoSimulacao calcularParcelas(org.example.dto.SimulacaoRequestDTO.ModeloEnvelopeSimulacao env, double taxaJuros) {
        double valor = env.getValorDesejado();
        int prazo = env.getPrazo();
        List<SimulacaoResponseDTO.Parcela> parcelas;
        if (valor <= 0 || prazo <= 0) {
            parcelas = gerarParcelasZeradas(prazo);
        } else {
            double amortizacao = valor / prazo;
            double saldoDevedor = valor;
            parcelas = new ArrayList<>();
            for (int i = 1; i <= prazo; i++) {
                double juros = saldoDevedor * (taxaJuros / 100.0);
                double valorPrestacao = amortizacao + juros;
                SimulacaoResponseDTO.Parcela parcela = new SimulacaoResponseDTO.Parcela();
                parcela.setNumero(i);
                parcela.setValorAmortizacao(round(amortizacao));
                parcela.setValorJuros(round(juros));
                parcela.setValorPrestacao(round(valorPrestacao));
                parcelas.add(parcela);
                saldoDevedor -= amortizacao;
            }
        }
        SimulacaoResponseDTO.ResultadoSimulacao resultado = new SimulacaoResponseDTO.ResultadoSimulacao();
        resultado.setTipo("SAC");
        resultado.setParcelas(parcelas);
        return resultado;
    }
}
