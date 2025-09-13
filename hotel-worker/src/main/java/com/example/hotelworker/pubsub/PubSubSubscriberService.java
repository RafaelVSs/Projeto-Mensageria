package com.example.hotelworker.pubsub;

import com.example.hotelworker.service.MessageProcessor;
import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.pubsub.v1.ProjectSubscriptionName;
import com.google.pubsub.v1.PubsubMessage;

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
        ProjectSubscriptionName subscriptionName = ProjectSubscriptionName.of(projectId, subscriptionId);

        MessageReceiver receiver = new MessageReceiver() {
            @Override
            public void receiveMessage(PubsubMessage message, AckReplyConsumer consumer) {
                String messageId = message.getMessageId();
                String data = message.getData().toStringUtf8();
                System.out.println("Received messageId=" + messageId);
                try {
                    processor.process(messageId, data);
                    consumer.ack();
                    System.out.println("Acked messageId=" + messageId);
                } catch (Exception e) {
                    System.err.println("Processing failed for messageId=" + messageId + ": " + e.getMessage());
                    e.printStackTrace();
                    consumer.nack();
                }
            }
        };

        subscriber = Subscriber.newBuilder(subscriptionName, receiver).build();
        subscriber.startAsync().awaitRunning();
    }

    public void stop() {
        if (subscriber != null) subscriber.stopAsync();
    }
}