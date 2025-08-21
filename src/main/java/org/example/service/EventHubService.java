package org.example.service;

import com.azure.messaging.eventhubs.EventHubClientBuilder;
import com.azure.messaging.eventhubs.EventHubProducerClient;
import com.azure.messaging.eventhubs.EventDataBatch;
import com.azure.messaging.eventhubs.EventData;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EventHubService {
    private final EventHubProducerClient producerClient;

    // Construtor para produção
    public EventHubService(
            @Value("${eventhub.connection-string}") String connectionString,
            @Value("${eventhub.name}") String eventHubName) {
        this.producerClient = new EventHubClientBuilder()
                .connectionString(connectionString, eventHubName)
                .buildProducerClient();
    }

    // Construtor para testes
    public EventHubService(EventHubProducerClient producerClient) {
        this.producerClient = producerClient;
    }

    // Construtor padrão para permitir injeção sem argumentos (necessário para alguns testes)
    public EventHubService() {
        this.producerClient = null;
    }

    @CircuitBreaker(name = "eventHubCB", fallbackMethod = "fallbackEnviarMensagem")
    @RateLimiter(name = "eventHubRL")
    public void enviarMensagem(String mensagem) {
        EventDataBatch batch = producerClient.createBatch();
        batch.tryAdd(new EventData(mensagem));
        producerClient.send(batch);
    }

    public void fallbackEnviarMensagem(String mensagem, Throwable t) {
        System.err.println("[CIRCUIT BREAKER] Falha ao enviar mensagem para EventHub: " + t.getMessage());
        // Aqui pode-se logar ou persistir a mensagem para reprocessamento futuro
    }
}
