package com.example.hotelworker;

import com.example.hotelworker.pubsub.PubSubSubscriberService;
import com.example.hotelworker.service.MessageProcessor;
import com.example.hotelworker.config.DatabaseConfig;

import javax.sql.DataSource;

public class Main {
    public static void main(String[] args) throws Exception {
        var dbUrl = System.getenv("JDBC_DATABASE_URL");
        var dbUser = System.getenv("JDBC_DATABASE_USER");
        var dbPass = System.getenv("JDBC_DATABASE_PASSWORD");
        var projectId = System.getenv("GCP_PROJECT_ID");
        var subscriptionId = System.getenv("PUBSUB_SUBSCRIPTION");

        if (dbUrl == null || projectId == null || subscriptionId == null) {
            System.err.println("Missing required env vars. Ensure JDBC_DATABASE_URL, GCP_PROJECT_ID and PUBSUB_SUBSCRIPTION are set.");
            System.exit(1);
        }

        DataSource ds = DatabaseConfig.createHikariDataSource(dbUrl, dbUser, dbPass);

        MessageProcessor processor = new MessageProcessor(ds);
        PubSubSubscriberService subscriberService = new PubSubSubscriberService(projectId, subscriptionId, processor);
        subscriberService.start();

        System.out.println("Subscriber started. Waiting for messages...");

        // prevent exit
        Thread.currentThread().join();
    }
}