package org.example.dto;

import java.util.List;

public class PaginatedSimulacaoResumoResponseDTO {
    private int pagina;
    private long qtdRegistros;
    private int qtdRegistrosPagina;
    private List<SimulacaoResumoCustomDTO> registros;

    public PaginatedSimulacaoResumoResponseDTO() {}
    public PaginatedSimulacaoResumoResponseDTO(int pagina, long qtdRegistros, int qtdRegistrosPagina, List<SimulacaoResumoCustomDTO> registros) {
        this.pagina = pagina;
        this.qtdRegistros = qtdRegistros;
        this.qtdRegistrosPagina = qtdRegistrosPagina;
        this.registros = registros;
    }
    public int getPagina() { return pagina; }
    public void setPagina(int pagina) { this.pagina = pagina; }
    public long getQtdRegistros() { return qtdRegistros; }
    public void setQtdRegistros(long qtdRegistros) { this.qtdRegistros = qtdRegistros; }
    public int getQtdRegistrosPagina() { return qtdRegistrosPagina; }
    public void setQtdRegistrosPagina(int qtdRegistrosPagina) { this.qtdRegistrosPagina = qtdRegistrosPagina; }
    public List<SimulacaoResumoCustomDTO> getRegistros() { return registros; }
    public void setRegistros(List<SimulacaoResumoCustomDTO> registros) { this.registros = registros; }
}

