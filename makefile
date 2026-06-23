APP_NAME=url-shortener
DOCKER_COMPOSE=docker compose

.PHONY: help run test build clean docker-build docker-up docker-down docker-logs docker-clean

help:
	@echo "Available commands:"
	@echo "  make run           Run the app locally"
	@echo "  make test          Run tests"
	@echo "  make build         Build jar"
	@echo "  make clean         Clean project"
	@echo "  make docker-build  Build Docker image"
	@echo "  make docker-up     Run app with Docker Compose"
	@echo "  make docker-down   Stop Docker Compose"
	@echo "  make docker-logs   Show Docker logs"
	@echo "  make docker-clean  Stop and remove Docker volumes"

run:
	./mvnw spring-boot:run

test:
	./mvnw clean test

build:
	./mvnw clean package

clean:
	./mvnw clean

docker-build:
	$(DOCKER_COMPOSE) build

docker-up:
	$(DOCKER_COMPOSE) up --build

docker-down:
	$(DOCKER_COMPOSE) down

docker-logs:
	$(DOCKER_COMPOSE) logs -f $(APP_NAME)

docker-clean:
	$(DOCKER_COMPOSE) down -v