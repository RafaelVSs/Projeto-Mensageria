package com.example.hotelworker.service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class MessageProcessor {

    private final DataSource dataSource;

    public MessageProcessor(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void process(String messageId, String message) throws Exception {
        // Exemplo: salvar a mensagem no banco
        try (Connection conn = dataSource.getConnection()) {
            String sql = "INSERT INTO messages(id, content) VALUES (?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, messageId);
                stmt.setString(2, message);
                stmt.executeUpdate();
            }
        }
        System.out.println("Mensagem processada: " + messageId);
    }
}
