package org.example.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class SimulacaoRequestDTO {
    @NotNull
    @Valid
    private ModeloEnvelopeSimulacao modeloEnvelopeSimulacao;

    public ModeloEnvelopeSimulacao getModeloEnvelopeSimulacao() {
        return modeloEnvelopeSimulacao;
    }

    public void setModeloEnvelopeSimulacao(ModeloEnvelopeSimulacao modeloEnvelopeSimulacao) {
        this.modeloEnvelopeSimulacao = modeloEnvelopeSimulacao;
    }

    public static class ModeloEnvelopeSimulacao {
        @NotNull
        @Min(1)
        private Double valorDesejado;
        @NotNull
        @Min(1)
        private Integer prazo;

        public Double getValorDesejado() {
            return valorDesejado;
        }

        public void setValorDesejado(Double valorDesejado) {
            this.valorDesejado = valorDesejado;
        }

        public Integer getPrazo() {
            return prazo;
        }

        public void setPrazo(Integer prazo) {
            this.prazo = prazo;
        }
    }
}
