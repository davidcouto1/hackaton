package org.example.dto;

public class SimulacaoResumoDTO {
    private Long idSimulacao;
    private Double valorDesejado;
    private Integer prazo;
    private Double valorTotalParcelas;

    public Long getIdSimulacao() { return idSimulacao; }
    public void setIdSimulacao(Long idSimulacao) { this.idSimulacao = idSimulacao; }
    public Double getValorDesejado() { return valorDesejado; }
    public void setValorDesejado(Double valorDesejado) { this.valorDesejado = valorDesejado; }
    public Integer getPrazo() { return prazo; }
    public void setPrazo(Integer prazo) { this.prazo = prazo; }
    public Double getValorTotalParcelas() { return valorTotalParcelas; }
    public void setValorTotalParcelas(Double valorTotalParcelas) { this.valorTotalParcelas = valorTotalParcelas; }
}

