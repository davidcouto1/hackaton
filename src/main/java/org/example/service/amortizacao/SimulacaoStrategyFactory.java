package org.example.service.amortizacao;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SimulacaoStrategyFactory {
    private final Map<String, AmortizacaoStrategy> strategyMap;

    public SimulacaoStrategyFactory(List<AmortizacaoStrategy> strategies) {
        this.strategyMap = strategies.stream()
                .collect(Collectors.toMap(AmortizacaoStrategy::getTipo, s -> s));
    }

    public AmortizacaoStrategy getStrategy(String tipo) {
        AmortizacaoStrategy strategy = strategyMap.get(tipo);
        if (strategy == null) {
            throw new IllegalArgumentException("Tipo de amortização não suportado: " + tipo);
        }
        return strategy;
    }
}
