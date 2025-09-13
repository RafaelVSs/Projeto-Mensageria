CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- HOTEL
CREATE TABLE IF NOT EXISTS hotel (
  id BIGSERIAL PRIMARY KEY,
  external_hotel_id BIGINT,
  name TEXT,
  created_at TIMESTAMP WITH TIME ZONE DEFAULT now()
);

-- QUARTO
CREATE TABLE IF NOT EXISTS quarto (
  id BIGSERIAL PRIMARY KEY,
  hotel_id BIGINT REFERENCES hotel(id) ON DELETE CASCADE,
  external_room_id BIGINT,
  room_number TEXT,
  created_at TIMESTAMP WITH TIME ZONE DEFAULT now()
);

-- CLIENTE
CREATE TABLE IF NOT EXISTS cliente (
  id BIGSERIAL PRIMARY KEY,
  external_client_id BIGINT,
  name TEXT NOT NULL,
  email TEXT,
  phone TEXT,
  created_at TIMESTAMP WITH TIME ZONE DEFAULT now(),
  UNIQUE (external_client_id)
);

-- RESERVA
CREATE TABLE IF NOT EXISTS reserva (
  id BIGSERIAL PRIMARY KEY,
  uuid UUID NOT NULL UNIQUE,
  external_reservation_id BIGINT,
  hotel_id BIGINT REFERENCES hotel(id),
  cliente_id BIGINT REFERENCES cliente(id),
  checkin DATE,
  checkout DATE,
  created_at TIMESTAMP WITH TIME ZONE,
  indexed_at TIMESTAMP WITH TIME ZONE DEFAULT now(),
  total_amount NUMERIC(12,2),
  raw_message JSONB,
  pubsub_message_id TEXT
);

-- QUARTO_RESERVADO
CREATE TABLE IF NOT EXISTS quarto_reservado (
  id BIGSERIAL PRIMARY KEY,
  reserva_id BIGINT REFERENCES reserva(id) ON DELETE CASCADE,
  quarto_id BIGINT REFERENCES quarto(id),
  external_room_id BIGINT,
  room_number TEXT,
  rate NUMERIC(12,2),
  nights INTEGER,
  total_room NUMERIC(12,2),
  details JSONB
);

-- Índices
CREATE INDEX IF NOT EXISTS idx_reserva_uuid ON reserva (uuid);
CREATE INDEX IF NOT EXISTS idx_reserva_cliente ON reserva (cliente_id);
CREATE INDEX IF NOT EXISTS idx_quarto_reservado_quarto ON quarto_reservado (quarto_id);
CREATE INDEX IF NOT EXISTS idx_reserva_hotel ON reserva (hotel_id);
