package org.example.service.strategy;

import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class SimulacaoStrategyFactory {
    private final Map<String, SimulacaoStrategy> strategyMap;

    public SimulacaoStrategyFactory(List<SimulacaoStrategy> strategies) {
        this.strategyMap = strategies.stream()
                .collect(Collectors.toMap(SimulacaoStrategy::getTipo, s -> s));
    }

    public SimulacaoStrategy getStrategy(String tipo) {
        if (tipo == null) {
            throw new IllegalArgumentException("Tipo de simulação não pode ser nulo");
        }
        SimulacaoStrategy strategy = strategyMap.get(tipo.toUpperCase());
        if (strategy == null) {
            throw new IllegalArgumentException("Tipo de simulação não suportado: " + tipo);
        }
        return strategy;
    }
}
