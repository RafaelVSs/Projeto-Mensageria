package com.example.hotelworker.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class HotelDao {
    public static long findOrCreateByExternal(Connection conn, long externalHotelId) throws SQLException {
        if (externalHotelId <= 0) {
            // create a generic hotel row and return its id
            String insert = "INSERT INTO hotel (external_hotel_id, name) VALUES (NULL, NULL) RETURNING id";
            try (PreparedStatement ps = conn.prepareStatement(insert)) {
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getLong(1);
                }
            }
        } else {
            String select = "SELECT id FROM hotel WHERE external_hotel_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(select)) {
                ps.setLong(1, externalHotelId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getLong(1);
                }
            }
            String insert = "INSERT INTO hotel (external_hotel_id, name) VALUES (?, NULL) RETURNING id";
            try (PreparedStatement ps = conn.prepareStatement(insert)) {
                ps.setLong(1, externalHotelId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getLong(1);
                }
            }
        }
        throw new SQLException("Unable to findOrCreate hotel");
    }
}