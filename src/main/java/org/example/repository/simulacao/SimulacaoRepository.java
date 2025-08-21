package org.example.repository.simulacao;

import org.example.model.Simulacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SimulacaoRepository extends JpaRepository<Simulacao, Long> {
    List<Simulacao> findByDataSimulacaoBetween(LocalDateTime start, LocalDateTime end);
    List<Simulacao> findByProduto(String produto);
}

