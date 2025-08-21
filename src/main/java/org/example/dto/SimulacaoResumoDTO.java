package org.example.dto;

public class SimulacaoResumoDTO {
    private Long idSimulacao;
    private Double valorDesejado;
    private Integer prazo;
    private Double valorTotalParcelasSac;
    private Double valorTotalParcelasPrice;

    public Long getIdSimulacao() { return idSimulacao; }
    public void setIdSimulacao(Long idSimulacao) { this.idSimulacao = idSimulacao; }
    public Double getValorDesejado() { return valorDesejado; }
    public void setValorDesejado(Double valorDesejado) { this.valorDesejado = valorDesejado; }
    public Integer getPrazo() { return prazo; }
    public void setPrazo(Integer prazo) { this.prazo = prazo; }
    public Double getValorTotalParcelasSac() { return valorTotalParcelasSac; }
    public void setValorTotalParcelasSac(Double valorTotalParcelasSac) { this.valorTotalParcelasSac = valorTotalParcelasSac; }
    public Double getValorTotalParcelasPrice() { return valorTotalParcelasPrice; }
    public void setValorTotalParcelasPrice(Double valorTotalParcelasPrice) { this.valorTotalParcelasPrice = valorTotalParcelasPrice; }
}
