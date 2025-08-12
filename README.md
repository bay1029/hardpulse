# Hardware Monitoring Starter (Spring Boot + Kafka)

Quick start to stream telemetry into Kafka, raise alerts, and log messages.

## Structure
- docker-compose.yml, .env, README.md, Makefile
- socket-emulator/ (Node TCP server)
- producer-gateway/ (Spring Boot producer)
- alert-service/ (Spring Boot alert consumer)
- log-writer-service/ (Spring Boot logging consumer)

## Quick start
1) docker compose up -d
2) Create topics (Makefile target: `make topics`)
3) Run emulator: `cd socket-emulator && npm install && npm start`
4) Run apps:
   - `cd producer-gateway && mvn spring-boot:run`
   - `cd alert-service && mvn spring-boot:run`
   - `cd log-writer-service && mvn spring-boot:run`
