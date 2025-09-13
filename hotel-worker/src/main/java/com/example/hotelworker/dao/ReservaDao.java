package com.example.hotelworker.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class ReservaDao {
    public static long insert(Connection conn, UUID uuid, long externalReservationId, long hotelId, long clienteId,
                              String checkin, String checkout, String createdAt, BigDecimal totalAmount, String rawJson, String pubsubMessageId) throws SQLException {
        // idempotency: try to insert with ON CONFLICT DO NOTHING and return existing id if present
        String insert = "INSERT INTO reserva (uuid, external_reservation_id, hotel_id, cliente_id, checkin, checkout, created_at, total_amount, raw_message, pubsub_message_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?::jsonb, ?) ON CONFLICT (uuid) DO NOTHING RETURNING id";
        try (PreparedStatement ps = conn.prepareStatement(insert)) {
            ps.setObject(1, uuid);
            if (externalReservationId > 0) ps.setLong(2, externalReservationId); else ps.setNull(2, java.sql.Types.BIGINT);
            if (hotelId > 0) ps.setLong(3, hotelId); else ps.setNull(3, java.sql.Types.BIGINT);
            if (clienteId > 0) ps.setLong(4, clienteId); else ps.setNull(4, java.sql.Types.BIGINT);
            ps.setString(5, checkin);
            ps.setString(6, checkout);
            ps.setString(7, createdAt);
            ps.setBigDecimal(8, totalAmount);
            ps.setString(9, rawJson);
            ps.setString(10, pubsubMessageId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getLong(1);
            }
        }
        // if the insert did nothing (conflict), select existing id
        String select = "SELECT id FROM reserva WHERE uuid = ?";
        try (PreparedStatement ps = conn.prepareStatement(select)) {
            ps.setObject(1, uuid);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getLong(1);
            }
        }
        throw new SQLException("Unable to insert or find reserva");
    }

    public static void insertQuartoReservado(Connection conn, long reservaId, long quartoId, long externalRoomId, String roomNumber,
                                             BigDecimal rate, int nights, BigDecimal totalRoom, String detailsJson) throws SQLException {
        String insert = "INSERT INTO quarto_reservado (reserva_id, quarto_id, external_room_id, room_number, rate, nights, total_room, details) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?::jsonb)";
        try (PreparedStatement ps = conn.prepareStatement(insert)) {
            ps.setLong(1, reservaId);
            if (quartoId > 0) ps.setLong(2, quartoId); else ps.setNull(2, java.sql.Types.BIGINT);
            if (externalRoomId > 0) ps.setLong(3, externalRoomId); else ps.setNull(3, java.sql.Types.BIGINT);
            ps.setString(4, roomNumber);
            ps.setBigDecimal(5, rate);
            ps.setInt(6, nights);
            ps.setBigDecimal(7, totalRoom);
            ps.setString(8, detailsJson);
            ps.executeUpdate();
        }
    }
}