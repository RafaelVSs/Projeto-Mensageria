package com.example.hotelworker;

import com.example.hotelworker.config.DatabaseConfig;
import com.example.hotelworker.pubsub.PubSubSubscriberService;
import com.example.hotelworker.service.MessageProcessor;

import javax.sql.DataSource;

public class Main {

    public static void main(String[] args) {
        String jdbcUrl = System.getenv("JDBC_DATABASE_URL");
        String dbUser = System.getenv("JDBC_DATABASE_USER");
        String dbPassword = System.getenv("JDBC_DATABASE_PASSWORD");

        String projectId = System.getenv("GCP_PROJECT_ID");
        String subscriptionId = System.getenv("PUBSUB_SUBSCRIPTION");

        if (jdbcUrl == null || dbUser == null || dbPassword == null
                || projectId == null || subscriptionId == null) {
            System.err.println("Erro: Variáveis de ambiente não configuradas corretamente.");
            System.exit(1);
        }

        // Criar DataSource
        DataSource dataSource = DatabaseConfig.createDataSource(jdbcUrl, dbUser, dbPassword);
        System.out.println("Banco de dados conectado com sucesso!");

        // Criar processador de mensagens
        MessageProcessor processor = new MessageProcessor(dataSource);

        // Inicializar Pub/Sub
        PubSubSubscriberService subscriberService = new PubSubSubscriberService(projectId, subscriptionId, processor);
        subscriberService.start();
        System.out.println("Hotel Worker iniciado e escutando mensagens do Pub/Sub...");

        // Hook para parada limpa
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Parando subscriber...");
            subscriberService.stop();
            System.out.println("Subscriber stopped.");
        }));

        // Mantém o programa rodando indefinidamente
        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Thread principal interrompida, encerrando...");
        }
    }