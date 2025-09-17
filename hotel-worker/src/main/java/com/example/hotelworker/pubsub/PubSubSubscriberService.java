package com.example.hotelworker.pubsub;

import com.example.hotelworker.service.MessageProcessor;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.pubsub.v1.ProjectSubscriptionName;

public class PubSubSubscriberService {

    private final String projectId;
    private final String subscriptionId;
    private final MessageProcessor processor;
    private Subscriber subscriber;

    public PubSubSubscriberService(String projectId, String subscriptionId, MessageProcessor processor) {
        this.projectId = projectId;
        this.subscriptionId = subscriptionId;
        this.processor = processor;
    }

    public void start() {
        // Criar objeto ProjectSubscriptionName usando projectId e subscriptionId separados
        ProjectSubscriptionName subscriptionName = ProjectSubscriptionName.of(projectId, subscriptionId);

        // Criar Subscriber
        subscriber = Subscriber.newBuilder(subscriptionName, (message, consumer) -> {
            try {
                processor.processMessage(message); // processa sua mensagem
                consumer.ack(); // ACK da mensagem
            } catch (Exception e) {
                consumer.nack(); // NACK se houver erro
                e.printStackTrace();
            }
        }).build();

        // Inicia o Subscriber
        subscriber.startAsync().awaitRunning();
        System.out.println("Subscriber iniciado para: " + subscriptionName);
    }

    public void stop() {
        if (subscriber != null) {
            subscriber.stopAsync();
            System.out.println("Subscriber parado.");
        }
    }
}
