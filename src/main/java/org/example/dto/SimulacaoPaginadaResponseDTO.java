package org.example.dto;

import java.util.List;

public class SimulacaoPaginadaResponseDTO {
    private int pagina;
    private int qtdRegistros;
    private int qtdRegistrosPagina;
    private List<SimulacaoResumoDTO> registros;

    public SimulacaoPaginadaResponseDTO() {}

    public SimulacaoPaginadaResponseDTO(int pagina, int qtdRegistros, int qtdRegistrosPagina, List<SimulacaoResumoDTO> registros) {
        this.pagina = pagina;
        this.qtdRegistros = qtdRegistros;
        this.qtdRegistrosPagina = qtdRegistrosPagina;
        this.registros = registros;
    }

    public int getPagina() {
        return pagina;
    }

    public void setPagina(int pagina) {
        this.pagina = pagina;
    }

    public int getQtdRegistros() {
        return qtdRegistros;
    }

    public void setQtdRegistros(int qtdRegistros) {
        this.qtdRegistros = qtdRegistros;
    }

    public int getQtdRegistrosPagina() {
        return qtdRegistrosPagina;
    }

    public void setQtdRegistrosPagina(int qtdRegistrosPagina) {
        this.qtdRegistrosPagina = qtdRegistrosPagina;
    }

    public List<SimulacaoResumoDTO> getRegistros() {
        return registros;
    }

    public void setRegistros(List<SimulacaoResumoDTO> registros) {
        this.registros = registros;
    }
}

