package org.example.service;

import org.example.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class EventHubQueueConsumer {
    private final EventHubService eventHubService;

    public EventHubQueueConsumer(EventHubService eventHubService) {
        this.eventHubService = eventHubService;
    }

    @RabbitListener(queues = RabbitMQConfig.EVENTHUB_QUEUE)
    public void receiveEnvelope(String envelopeJson) {
        eventHubService.enviarMensagem(envelopeJson);
    }
}

