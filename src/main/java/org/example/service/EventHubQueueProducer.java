package org.example.service;

import org.example.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class EventHubQueueProducer {
    private final RabbitTemplate rabbitTemplate;

    public EventHubQueueProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendEnvelopeToQueue(String envelopeJson) {
        rabbitTemplate.convertAndSend(RabbitMQConfig.EVENTHUB_QUEUE, envelopeJson);
    }
}

