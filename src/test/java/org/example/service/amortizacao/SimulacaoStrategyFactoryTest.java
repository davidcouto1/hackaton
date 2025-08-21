package org.example.service.amortizacao;

import org.example.service.strategy.PriceSimulacaoStrategy;
import org.example.service.strategy.SacSimulacaoStrategy;
import org.example.service.strategy.SimulacaoStrategyFactory;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import static org.junit.jupiter.api.Assertions.*;

class SimulacaoStrategyFactoryTest {
    @Test
    void getStrategyDeveRetornarStrategyCorreta() {
        PriceSimulacaoStrategy price = new PriceSimulacaoStrategy();
        SacSimulacaoStrategy sac = new SacSimulacaoStrategy();
        SimulacaoStrategyFactory factory = new SimulacaoStrategyFactory(Arrays.asList(price, sac));
        assertEquals(price, factory.getStrategy("PRICE"));
        assertEquals(sac, factory.getStrategy("SAC"));
    }

    @Test
    void getStrategyTipoInvalidoDeveLancarExcecao() {
        SimulacaoStrategyFactory factory = new SimulacaoStrategyFactory(Arrays.asList(new PriceSimulacaoStrategy()));
        assertThrows(IllegalArgumentException.class, () -> factory.getStrategy("INEXISTENTE"));
    }

    @Test
    void getStrategyNuloDeveLancarExcecao() {
        SimulacaoStrategyFactory factory = new SimulacaoStrategyFactory(Arrays.asList(new PriceSimulacaoStrategy()));
        assertThrows(IllegalArgumentException.class, () -> factory.getStrategy(null));
    }
}
