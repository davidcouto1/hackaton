package org.example.config;

import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    public static final String EVENTHUB_QUEUE = "eventhub-simulacoes";

    @Bean
    public Queue eventHubQueue(@Value("${eventhub.queue:eventhub-simulacoes}") String queueName) {
        return new Queue(queueName, true);
    }
}
