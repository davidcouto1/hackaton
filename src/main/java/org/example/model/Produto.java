package org.example.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "PRODUTO")
public class Produto {
    @Id
    @Column(name = "CO_PRODUTO")
    private Integer id;

    @Column(name = "NO_PRODUTO")
    private String nome;

    @Column(name = "PC_TAXA_JUROS")
    private BigDecimal taxaJuros;

    @Column(name = "NU_MINIMO_MESES")
    private Short prazoMinimo;

    @Column(name = "NU_MAXIMO_MESES")
    private Short prazoMaximo;

    @Column(name = "VR_MINIMO")
    private BigDecimal valorMinimo;

    @Column(name = "VR_MAXIMO")
    private BigDecimal valorMaximo;

    // Getters e setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public BigDecimal getTaxaJuros() { return taxaJuros; }
    public void setTaxaJuros(BigDecimal taxaJuros) { this.taxaJuros = taxaJuros; }
    public Short getPrazoMinimo() { return prazoMinimo; }
    public void setPrazoMinimo(Short prazoMinimo) { this.prazoMinimo = prazoMinimo; }
    public Short getPrazoMaximo() { return prazoMaximo; }
    public void setPrazoMaximo(Short prazoMaximo) { this.prazoMaximo = prazoMaximo; }
    public BigDecimal getValorMinimo() { return valorMinimo; }
    public void setValorMinimo(BigDecimal valorMinimo) { this.valorMinimo = valorMinimo; }
    public BigDecimal getValorMaximo() { return valorMaximo; }
    public void setValorMaximo(BigDecimal valorMaximo) { this.valorMaximo = valorMaximo; }
}
