package org.example.dto;

public class SimulacaoResumoCustomDTO {
    private Long idSimulacao;
    private Double valorDesejado;
    private Integer prazo;
    private Double valorTotalParcelas;
    private Double valorTotalParcelasSac;
    private Double valorTotalParcelasPrice;

    public SimulacaoResumoCustomDTO() {}
    public SimulacaoResumoCustomDTO(Long idSimulacao, Double valorDesejado, Integer prazo, Double valorTotalParcelasSac, Double valorTotalParcelasPrice) {
        this.idSimulacao = idSimulacao;
        this.valorDesejado = valorDesejado;
        this.prazo = prazo;
        this.valorTotalParcelasSac = valorTotalParcelasSac;
        this.valorTotalParcelasPrice = valorTotalParcelasPrice;
    }
    public Long getIdSimulacao() { return idSimulacao; }
    public void setIdSimulacao(Long idSimulacao) { this.idSimulacao = idSimulacao; }
    public Double getValorDesejado() { return valorDesejado; }
    public void setValorDesejado(Double valorDesejado) { this.valorDesejado = valorDesejado; }
    public Integer getPrazo() { return prazo; }
    public void setPrazo(Integer prazo) { this.prazo = prazo; }
    public Double getValorTotalParcelas() { return valorTotalParcelas; }
    public void setValorTotalParcelas(Double valorTotalParcelas) { this.valorTotalParcelas = valorTotalParcelas; }
    public Double getValorTotalParcelasSac() { return valorTotalParcelasSac; }
    public void setValorTotalParcelasSac(Double valorTotalParcelasSac) { this.valorTotalParcelasSac = valorTotalParcelasSac; }
    public Double getValorTotalParcelasPrice() { return valorTotalParcelasPrice; }
    public void setValorTotalParcelasPrice(Double valorTotalParcelasPrice) { this.valorTotalParcelasPrice = valorTotalParcelasPrice; }
}
