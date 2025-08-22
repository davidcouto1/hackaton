package org.example.service;

import org.example.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EventHubQueueProducer {
    private final RabbitTemplate rabbitTemplate;
    private final String queueName;

    public EventHubQueueProducer(RabbitTemplate rabbitTemplate, @Value("${eventhub.queue:eventhub-simulacoes}") String queueName) {
        this.rabbitTemplate = rabbitTemplate;
        this.queueName = queueName;
    }

    public void sendEnvelopeToQueue(String envelopeJson) {
        rabbitTemplate.convertAndSend(queueName, envelopeJson);
    }
}
