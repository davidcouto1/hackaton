package org.example.service.strategy;

import java.util.ArrayList;
import java.util.List;
import org.example.dto.SimulacaoResponseDTO;
import org.example.model.Simulacao;
import org.example.model.Produto;
import org.springframework.stereotype.Component;

@Component
public class PriceSimulacaoStrategy implements SimulacaoStrategy {
    @Override
    public List<SimulacaoResponseDTO.Parcela> calcular(Simulacao simulacao, Produto produto) {
        double valor = simulacao.getValorSolicitado();
        int prazo = simulacao.getPrazo();
        double i = simulacao.getTaxaJuros() / 100.0;
        List<SimulacaoResponseDTO.Parcela> parcelas = new ArrayList<>();
        if (prazo <= 0 || valor <= 0) {
            return gerarParcelasZeradas(prazo);
        } else if (i == 0.0 || prazo == 1) {
            return gerarParcelasSemJuros(valor, prazo);
        }
        double valorPrestacao = calcularPrestacaoPrice(valor, prazo, i);
        double saldoDevedor = valor;
        for (int n = 1; n <= prazo; n++) {
            SimulacaoResponseDTO.Parcela parcela = calcularParcelaPrice(n, saldoDevedor, i, valorPrestacao);
            parcelas.add(parcela);
            saldoDevedor -= parcela.getValorAmortizacao();
        }
        return parcelas;
    }

    private double calcularPrestacaoPrice(double valor, int prazo, double i) {
        return valor * (i * Math.pow(1 + i, prazo)) / (Math.pow(1 + i, prazo) - 1);
    }

    private SimulacaoResponseDTO.Parcela calcularParcelaPrice(int numero, double saldoDevedor, double i, double valorPrestacao) {
        double juros = saldoDevedor * i;
        double amortizacao = valorPrestacao - juros;
        SimulacaoResponseDTO.Parcela parcela = new SimulacaoResponseDTO.Parcela();
        parcela.setNumero(numero);
        parcela.setValorAmortizacao(round(amortizacao));
        parcela.setValorJuros(round(juros));
        parcela.setValorPrestacao(round(valorPrestacao));
        return parcela;
    }

    private List<SimulacaoResponseDTO.Parcela> gerarParcelasSemJuros(double valor, int prazo) {
        List<SimulacaoResponseDTO.Parcela> parcelas = new ArrayList<>();
        double valorPrestacao = valor / prazo;
        for (int n = 1; n <= prazo; n++) {
            SimulacaoResponseDTO.Parcela parcela = new SimulacaoResponseDTO.Parcela();
            parcela.setNumero(n);
            parcela.setValorAmortizacao(round(valorPrestacao));
            parcela.setValorJuros(0.0);
            parcela.setValorPrestacao(round(valorPrestacao));
            parcelas.add(parcela);
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
        return "PRICE";
    }

    @Override
    public SimulacaoResponseDTO.ResultadoSimulacao calcularParcelas(org.example.dto.SimulacaoRequestDTO.ModeloEnvelopeSimulacao env, double taxaJuros) {
        double valor = env.getValorDesejado();
        int prazo = env.getPrazo();
        double i = taxaJuros / 100.0;
        List<SimulacaoResponseDTO.Parcela> parcelas;
        if (prazo <= 0 || valor <= 0) {
            parcelas = gerarParcelasZeradas(prazo);
        } else if (prazo == 1) {
            // Para prazo 1, prestação igual ao valor solicitado, sem juros
            parcelas = gerarParcelasSemJuros(valor, 1);
        } else if (i == 0.0) {
            parcelas = gerarParcelasSemJuros(valor, prazo);
        } else {
            double valorPrestacao = calcularPrestacaoPrice(valor, prazo, i);
            double saldoDevedor = valor;
            parcelas = new ArrayList<>();
            for (int n = 1; n <= prazo; n++) {
                SimulacaoResponseDTO.Parcela parcela = calcularParcelaPrice(n, saldoDevedor, i, valorPrestacao);
                parcelas.add(parcela);
                saldoDevedor -= parcela.getValorAmortizacao();
            }
        }
        SimulacaoResponseDTO.ResultadoSimulacao resultado = new SimulacaoResponseDTO.ResultadoSimulacao();
        resultado.setTipo(getTipo());
        resultado.setParcelas(parcelas);
        return resultado;
    }
}
