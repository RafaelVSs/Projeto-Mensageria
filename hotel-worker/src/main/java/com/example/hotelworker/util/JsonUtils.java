package com.example.hotelworker.util;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtils {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static ObjectMapper mapper() {
        return MAPPER;
    }
}