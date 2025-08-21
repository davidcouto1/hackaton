package org.example.dto;

import java.util.List;

public class SimulacaoResponseDTO {
    private ModeloEnvelopeRetornoSimulacao modeloEnvelopeRetornoSimulacao;

    public ModeloEnvelopeRetornoSimulacao getModeloEnvelopeRetornoSimulacao() {
        return modeloEnvelopeRetornoSimulacao;
    }

    public void setModeloEnvelopeRetornoSimulacao(ModeloEnvelopeRetornoSimulacao modeloEnvelopeRetornoSimulacao) {
        this.modeloEnvelopeRetornoSimulacao = modeloEnvelopeRetornoSimulacao;
    }

    public static class ModeloEnvelopeRetornoSimulacao {
        private Long idSimulacao;
        private Integer codigoProduto;
        private String descricaoProduto;
        private Double taxaJuros;
        private List<ResultadoSimulacao> resultadoSimulacao;

        // getters e setters
        public Long getIdSimulacao() { return idSimulacao; }
        public void setIdSimulacao(Long idSimulacao) { this.idSimulacao = idSimulacao; }
        public Integer getCodigoProduto() { return codigoProduto; }
        public void setCodigoProduto(Integer codigoProduto) { this.codigoProduto = codigoProduto; }
        public String getDescricaoProduto() { return descricaoProduto; }
        public void setDescricaoProduto(String descricaoProduto) { this.descricaoProduto = descricaoProduto; }
        public Double getTaxaJuros() { return taxaJuros; }
        public void setTaxaJuros(Double taxaJuros) { this.taxaJuros = taxaJuros; }
        public List<ResultadoSimulacao> getResultadoSimulacao() { return resultadoSimulacao; }
        public void setResultadoSimulacao(List<ResultadoSimulacao> resultadoSimulacao) { this.resultadoSimulacao = resultadoSimulacao; }
    }

    public static class ResultadoSimulacao {
        private String tipo;
        private List<Parcela> parcelas;
        public String getTipo() { return tipo; }
        public void setTipo(String tipo) { this.tipo = tipo; }
        public List<Parcela> getParcelas() { return parcelas; }
        public void setParcelas(List<Parcela> parcelas) { this.parcelas = parcelas; }
    }

    public static class Parcela {
        private Integer numero;
        private Double valorAmortizacao;
        private Double valorJuros;
        private Double valorPrestacao;
        public Integer getNumero() { return numero; }
        public void setNumero(Integer numero) { this.numero = numero; }
        public Double getValorAmortizacao() { return valorAmortizacao; }
        public void setValorAmortizacao(Double valorAmortizacao) { this.valorAmortizacao = valorAmortizacao; }
        public Double getValorJuros() { return valorJuros; }
        public void setValorJuros(Double valorJuros) { this.valorJuros = valorJuros; }
        public Double getValorPrestacao() { return valorPrestacao; }
        public void setValorPrestacao(Double valorPrestacao) { this.valorPrestacao = valorPrestacao; }
    }
}

