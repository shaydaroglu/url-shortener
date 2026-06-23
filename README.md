# URL Shortener

A small URL shortener application built with Spring Boot.
It allows users to submit a long URL and receive a 4-character short URL. Visiting the short URL redirects the client to the original URL.

The project is intentionally kept simple and focused on the MVP requirements, while still using clean backend practices such as validation, persistence, Flyway migrations, error handling, tests, and Docker support.

## Tech Stack

* Java 21
* Spring Boot
* Spring Web
* Spring Data JPA
* H2 Database
* Flyway
* Jakarta Validation
* Lombok
* JUnit 5
* Mockito
* Docker / Docker Compose

## Features

* Create a short URL from a valid original URL
* Generate 4-character short codes
* Redirect short URLs to the original URL
* Validate incoming requests
* Return structured error responses
* Persist data with H2
* Manage schema with Flyway
* Run locally or with Docker
* Basic test coverage for service, controller, and persistence layers

## API Endpoints

### Create short URL

```http
POST /
Content-Type: application/json
```

Request:

```json
{
  "original_url": "https://www.google.com/search?q=url+shortener"
}
```

Response:

```http
201 Created
Location: http://localhost:8080/byv5
```

Body:

```json
{
  "original_url": "https://www.google.com/search?q=url+shortener",
  "short_code": "byv5",
  "shortened_url": "http://localhost:8080/byv5"
}
```

### Redirect short URL

```http
GET /{shortCode}
```

Example:

```http
GET /byv5
```

Response:

```http
302 Found
Location: https://www.google.com/search?q=url+shortener
```

## Validation

The original URL must:

* Not be blank
* Start with `http://` or `https://`
* Have a valid domain
* Not exceed 2048 characters

Invalid request example:

```json
{
  "original_url": "https://youtube"
}
```

Error response:

```json
{
  "title": "Validation failed",
  "status": 400,
  "detail": "original_url: URL must be a valid http or https URL",
  "timestamp": "2026-06-24T00:00:00+02:00"
}
```

## Short Code Generation

Short codes are generated from the database ID.

The flow is:

1. Save the original URL without a short code
2. Database generates a unique ID
3. Application generates a 4-character Base62 short code from that ID
4. Application updates the same row with the generated short code

Base62 alphabet:

```text
0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ
```

A 4-character Base62 code gives:

```text
62^4 = 14,776,336 possible short codes
```

To avoid obvious sequential codes, the ID is scrambled before Base62 encoding:

```text
scrambled = (value * multiplier + offset) % maxCodes
```

This keeps generation deterministic and collision-free while making the output less predictable than simple sequential encoding.

## Database

The project uses H2.

Local profile uses in-memory H2:

```properties
jdbc:h2:mem:urlshortener
```

Docker profile uses file-based H2:

```properties
jdbc:h2:file:/app/data/urlshortener
```

Schema changes are managed with Flyway migrations under:

```text
src/main/resources/db/migration
```

## Profiles

The application has three properties files:

```text
application.properties
application-local.properties
application-docker.properties
```

Default profile:

```properties
spring.profiles.active=local
```

Docker overrides this with:

```yaml
SPRING_PROFILES_ACTIVE: docker
```

## Run Locally

```bash
./mvnw spring-boot:run
```

The application will start on:

```text
http://localhost:8080
```

H2 console:

```text
http://localhost:8080/h2-console
```

For local profile, use:

```text
JDBC URL: jdbc:h2:mem:urlshortener;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1
User: sa
Password:
```

## Run Tests

```bash
./mvnw clean test
```

## Run with Docker

Build and start:

```bash
docker compose up --build
```

Stop:

```bash
docker compose down
```

Remove containers and volumes:

```bash
docker compose down -v
```

H2 data is stored in a Docker volume when using the Docker profile.

## Makefile Commands

The project includes a `Makefile` for convenience.

```bash
make run           # Run locally
make test          # Run tests
make build         # Build jar
make clean         # Clean project
make docker-build  # Build Docker image
make docker-up     # Run Docker Compose
make docker-down   # Stop Docker Compose
make docker-logs   # Show Docker logs
make docker-clean  # Stop and remove Docker volumes
```

## Example Curl Commands

Create a short URL:

```bash
curl -i -X POST http://localhost:8080 \
  -H "Content-Type: application/json" \
  -d '{"original_url":"https://www.google.com/search?q=url+shortener"}'
```

Redirect:

```bash
curl -i http://localhost:8080/byv5
```

Invalid URL:

```bash
curl -i -X POST http://localhost:8080 \
  -H "Content-Type: application/json" \
  -d '{"original_url":"https://youtube"}'
```

## Design Notes

This project uses a lightweight hexagonal-style structure:

```text
domain
application
adapter.in.web
adapter.out.persistence
```

The application core works with domain objects and repository ports.
The persistence adapter maps between domain objects and JPA entities.

This keeps the business logic separate from persistence and web concerns without making the project unnecessarily complex.

## Trade-offs

The project uses H2 for simplicity. For a real production system, PostgreSQL would be a better choice.

The short code length is fixed to 4 characters because of the assignment requirement. This limits the total number of available codes to around 14.7 million.

The short code is generated from the database ID. This avoids collisions, but it is not cryptographically secure. The scrambling step makes codes less obviously sequential, but it should not be treated as security.

There is no authentication, analytics, rate limiting, caching, or abuse detection because they are outside the MVP scope.

## Possible Future Improvements

* Replace H2 with PostgreSQL
* Add Redis cache for fast redirect lookups
* Add click analytics
* Add rate limiting
* Add custom aliases
* Add expiration support
* Add soft deletion / deactivation
* Add OpenAPI documentation
* Add more integration tests
* Add monitoring and structured logging
