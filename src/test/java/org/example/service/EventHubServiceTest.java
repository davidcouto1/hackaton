package org.example.service;

import com.azure.messaging.eventhubs.EventHubProducerClient;
import com.azure.messaging.eventhubs.EventDataBatch;
import com.azure.messaging.eventhubs.EventData;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.*;

class EventHubServiceTest {
    @Test
    void testEnviarMensagem() {
        EventHubProducerClient producerClient = Mockito.mock(EventHubProducerClient.class);
        EventDataBatch batch = Mockito.mock(EventDataBatch.class);
        Mockito.when(producerClient.createBatch()).thenReturn(batch);
        Mockito.when(batch.tryAdd(Mockito.any(EventData.class))).thenReturn(true);
        Mockito.doNothing().when(producerClient).send(batch);
        // Usa o novo construtor para injetar o mock
        EventHubService service = new EventHubService(producerClient);
        service.enviarMensagem("teste");
        Mockito.verify(producerClient, Mockito.times(1)).send(batch);
    }
}
