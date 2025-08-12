up:
	docker compose up -d
down:
	docker compose down -v
topics:
	docker compose exec kafka kafka-topics.sh --bootstrap-server kafka:9092 --create --topic telemetry.raw --partitions 6 --replication-factor 1 || true
	docker compose exec kafka kafka-topics.sh --bootstrap-server kafka:9092 --create --topic telemetry.alerts --partitions 3 --replication-factor 1 || true
	docker compose exec kafka kafka-topics.sh --bootstrap-server kafka:9092 --create --topic telemetry.raw.DLT --partitions 1 --replication-factor 1 || true
