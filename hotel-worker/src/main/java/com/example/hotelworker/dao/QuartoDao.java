package com.example.hotelworker.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class QuartoDao {
    public static long findOrCreate(Connection conn, long hotelId, long externalRoomId, String roomNumber) throws SQLException {
        // try find by external_room_id and hotel
        if (externalRoomId > 0) {
            String sel = "SELECT id FROM quarto WHERE hotel_id = ? AND external_room_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sel)) {
                ps.setLong(1, hotelId);
                ps.setLong(2, externalRoomId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getLong(1);
                }
            }
            String ins = "INSERT INTO quarto (hotel_id, external_room_id, room_number) VALUES (?, ?, ?) RETURNING id";
            try (PreparedStatement ps = conn.prepareStatement(ins)) {
                ps.setLong(1, hotelId);
                ps.setLong(2, externalRoomId);
                ps.setString(3, roomNumber);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getLong(1);
                }
            }
        } else {
            // fallback: try by hotel_id + room_number
            String sel = "SELECT id FROM quarto WHERE hotel_id = ? AND room_number = ?";
            try (PreparedStatement ps = conn.prepareStatement(sel)) {
                ps.setLong(1, hotelId);
                ps.setString(2, roomNumber);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getLong(1);
                }
            }
            String ins = "INSERT INTO quarto (hotel_id, external_room_id, room_number) VALUES (?, NULL, ?) RETURNING id";
            try (PreparedStatement ps = conn.prepareStatement(ins)) {
                ps.setLong(1, hotelId);
                ps.setString(2, roomNumber);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getLong(1);
                }
            }
        }
        throw new SQLException("Unable to findOrCreate quarto");
    }
}