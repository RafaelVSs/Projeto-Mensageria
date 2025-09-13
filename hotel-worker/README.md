# Hotel Worker (Mensageria - Java)

Este projeto implementa um **worker em Java** que consome mensagens do **Pub/Sub (GCP)** e grava os dados em um banco **PostgreSQL**.

## Como rodar

### 1. Pré-requisitos
- JDK 17+
- Maven 3.6+
- Docker (para subir Postgres local)
- Credencial GCP (service account JSON)

### 2. Configurar Postgres
```bash
docker run --name hotel-postgres -e POSTGRES_PASSWORD=pass -e POSTGRES_DB=hoteldb -p 5432:5432 -d postgres:15
cat src/main/resources/ddl.sql | docker exec -i hotel-postgres psql -U postgres -d hoteldb
