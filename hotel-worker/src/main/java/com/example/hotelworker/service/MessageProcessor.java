package com.example.hotelworker.service;

import com.example.hotelworker.dao.ClienteDao;
import com.example.hotelworker.dao.HotelDao;
import com.example.hotelworker.dao.QuartoDao;
import com.example.hotelworker.dao.ReservaDao;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;

public class MessageProcessor {
    private final DataSource ds;
    private final ObjectMapper mapper = new ObjectMapper();

    public MessageProcessor(DataSource ds) {
        this.ds = ds;
    }

    public void process(String pubsubMessageId, String payloadJson) throws Exception {
        JsonNode node = mapper.readTree(payloadJson);

        String uuidStr = node.path("reservationUuid").asText(null);
        if (uuidStr == null || uuidStr.isBlank()) {
            throw new IllegalArgumentException("reservationUuid is required");
        }
        UUID reservationUuid = UUID.fromString(uuidStr);
        long externalReservationId = node.path("reservationId").asLong(0);
        long hotelExternalId = node.path("hotelId").asLong(0);

        JsonNode clientNode = node.path("client");
        long clientExternalId = clientNode.path("clientId").asLong(0);
        String clientName = clientNode.path("name").asText(null);
        String clientEmail = clientNode.path("email").asText(null);
        String clientPhone = clientNode.path("phone").asText(null);

        JsonNode roomsNode = node.path("rooms");

        try (Connection conn = ds.getConnection()) {
            conn.setAutoCommit(false);
            try {
                long hotelId = HotelDao.findOrCreateByExternal(conn, hotelExternalId);
                long clienteId = ClienteDao.findOrCreateByExternal(conn, clientExternalId, clientName, clientEmail, clientPhone);

                BigDecimal total = BigDecimal.ZERO;
                if (roomsNode.isArray()) {
                    for (JsonNode r : roomsNode) {
                        BigDecimal rate = new BigDecimal(r.path("rate").asText("0"));
                        int nights = r.path("nights").asInt(1);
                        total = total.add(rate.multiply(BigDecimal.valueOf(nights)));
                    }
                }

                long reservaId = ReservaDao.insert(conn, reservationUuid, externalReservationId, hotelId, clienteId,
                        node.path("checkin").asText(null), node.path("checkout").asText(null), node.path("createdAt").asText(null),
                        total, payloadJson, pubsubMessageId);

                if (roomsNode.isArray()) {
                    for (JsonNode r : roomsNode) {
                        long externalRoomId = r.path("roomId").asLong(0);
                        String roomNumber = r.path("roomNumber").asText(null);
                        BigDecimal rate = new BigDecimal(r.path("rate").asText("0"));
                        int nights = r.path("nights").asInt(1);
                        BigDecimal totalRoom = rate.multiply(BigDecimal.valueOf(nights));

                        long quartoId = QuartoDao.findOrCreate(conn, hotelId, externalRoomId, roomNumber);
                        ReservaDao.insertQuartoReservado(conn, reservaId, quartoId, externalRoomId, roomNumber, rate, nights, totalRoom, r.toString());
                    }
                }

                conn.commit();
            } catch (Exception ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }
}