package org.example.repository.produto;

import org.example.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    Produto findByNome(String nome);
    // Métodos de consulta customizados podem ser adicionados aqui
}

