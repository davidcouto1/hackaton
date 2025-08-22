package org.example.repository.auditoria;

import org.example.model.Auditoria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditoriaRepository extends JpaRepository<Auditoria, Long> {
    // Nenhum método adicional necessário
}

