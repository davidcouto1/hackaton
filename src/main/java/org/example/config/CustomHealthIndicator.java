package org.example.config;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile({"dev", "test"})
public class CustomHealthIndicator implements HealthIndicator {
    @Override
    public Health health() {
        return Health.up().withDetail("custom", "Always UP in dev/test").build();
    }
}

