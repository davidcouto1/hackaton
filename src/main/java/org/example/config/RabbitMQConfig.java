package org.example.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    public static final String EVENTHUB_QUEUE = "eventhub-simulacoes";

    @Bean
    public Queue eventHubQueue() {
        return new Queue(EVENTHUB_QUEUE, true);
    }
}

