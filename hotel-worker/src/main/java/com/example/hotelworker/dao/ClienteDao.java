package com.example.hotelworker.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ClienteDao {
    public static long findOrCreateByExternal(Connection conn, long externalClientId, String name, String email, String phone) throws SQLException {
        if (externalClientId > 0) {
            String sel = "SELECT id FROM cliente WHERE external_client_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sel)) {
                ps.setLong(1, externalClientId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getLong(1);
                }
            }
            String ins = "INSERT INTO cliente (external_client_id, name, email, phone) VALUES (?, ?, ?, ?) RETURNING id";
            try (PreparedStatement ps = conn.prepareStatement(ins)) {
                ps.setLong(1, externalClientId);
                ps.setString(2, name);
                ps.setString(3, email);
                ps.setString(4, phone);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getLong(1);
                }
            }
        } else {
            // fallback: create by name (no external id)
            String ins = "INSERT INTO cliente (external_client_id, name, email, phone) VALUES (NULL, ?, ?, ?) RETURNING id";
            try (PreparedStatement ps = conn.prepareStatement(ins)) {
                ps.setString(1, name);
                ps.setString(2, email);
                ps.setString(3, phone);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getLong(1);
                }
            }
        }
        throw new SQLException("Unable to findOrCreate cliente");
    }
}