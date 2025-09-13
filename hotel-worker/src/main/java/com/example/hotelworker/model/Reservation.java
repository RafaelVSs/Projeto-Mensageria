package com.example.hotelworker.model;

import com.fasterxml.jackson.databind.JsonNode;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class Reservation {
    public UUID reservationUuid;
    public long reservationId;
    public long hotelId;
    public String checkin;
    public String checkout;
    public String createdAt;
    public Client client;
    public List<RoomReserved> rooms;
    public BigDecimal totalAmount;
    public JsonNode raw;
}