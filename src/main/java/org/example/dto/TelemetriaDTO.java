package org.example.dto;

import java.util.List;

public class TelemetriaDTO {
    private String dataReferencia;
    private List<EndpointTelemetria> listaEndpoints;

    public String getDataReferencia() {
        return dataReferencia;
    }
    public void setDataReferencia(String dataReferencia) {
        this.dataReferencia = dataReferencia;
    }
    public List<EndpointTelemetria> getListaEndpoints() {
        return listaEndpoints;
    }
    public void setListaEndpoints(List<EndpointTelemetria> listaEndpoints) {
        this.listaEndpoints = listaEndpoints;
    }

    public static class EndpointTelemetria {
        private String nomeApi;
        private int qtdRequisicoes;
        private long tempoMedio;
        private long tempoMinimo;
        private long tempoMaximo;
        private double percentualSucesso;

        public String getNomeApi() { return nomeApi; }
        public void setNomeApi(String nomeApi) { this.nomeApi = nomeApi; }
        public int getQtdRequisicoes() { return qtdRequisicoes; }
        public void setQtdRequisicoes(int qtdRequisicoes) { this.qtdRequisicoes = qtdRequisicoes; }
        public long getTempoMedio() { return tempoMedio; }
        public void setTempoMedio(long tempoMedio) { this.tempoMedio = tempoMedio; }
        public long getTempoMinimo() { return tempoMinimo; }
        public void setTempoMinimo(long tempoMinimo) { this.tempoMinimo = tempoMinimo; }
        public long getTempoMaximo() { return tempoMaximo; }
        public void setTempoMaximo(long tempoMaximo) { this.tempoMaximo = tempoMaximo; }
        public double getPercentualSucesso() { return percentualSucesso; }
        public void setPercentualSucesso(double percentualSucesso) { this.percentualSucesso = percentualSucesso; }
    }
}

