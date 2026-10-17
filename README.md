# Job Application Tracker

A containerized, microservices-based REST API for managing job applications and user profiles, built with Java and Spring Boot.

## Architecture

- **API Gateway:** Routes incoming requests to backend services.
- **Application Service:** Creates, retrieves, updates, and deletes job applications.
- **User Service:** Manages user profiles.
- **Discovery Server:** Uses Netflix Eureka for service registration and discovery.
- **MySQL:** Persists application and user data.
- **Docker Compose:** Runs the services together.

## Technology Stack

Java 17, Spring Boot, Spring Cloud Gateway, Netflix Eureka, Spring Data JPA, Hibernate, MySQL, Maven, Docker, REST APIs, JUnit, Mockito, and Postman.

## Services and Ports

| Component | Port |
|---|---:|
| API Gateway | 8080 |
| Application Service | 8081 |
| User Service | 8082 |
| Eureka Dashboard | 8761 |
| MySQL (host) | 3307 |

## Run Locally

Prerequisites: Docker Desktop with Docker Compose.

From the repository root, run:

```powershell
docker compose -p job-tracker up --build -d
```

Check service status:

```powershell
docker compose -p job-tracker ps
```

Open the Eureka dashboard at http://localhost:8761.

Stop the containers while preserving database data:

```powershell
docker compose -p job-tracker down
```

## API Endpoints

### Job Applications

Base URL: `http://localhost:8080/api/applications`

| Method | Endpoint | Purpose |
|---|---|---|
| GET | `/api/applications` | List applications |
| GET | `/api/applications/{id}` | Get an application |
| POST | `/api/applications` | Create an application |
| PUT | `/api/applications/{id}` | Update an application |
| DELETE | `/api/applications/{id}` | Delete an application |

Example request body:

```json
{
  "companyName": "Example Company",
  "jobTitle": "Software Engineer",
  "jobUrl": "https://example.com/jobs/123",
  "status": "APPLIED",
  "notes": "Applied through the company careers portal"
}
```

### User Profiles

Base URL: `http://localhost:8080/api/users`

| Method | Endpoint | Purpose |
|---|---|---|
| GET | `/api/users` | List user profiles |
| GET | `/api/users/{id}` | Get a profile |
| POST | `/api/users` | Create a profile |
| DELETE | `/api/users/{id}` | Delete a profile |

Example request body:

```json
{
  "name": "Demo User",
  "email": "demo@example.com"
}
```

## Testing

Controller tests cover successful requests, validation, missing records, and CRUD behavior. The application and user services each have nine controller tests.

Run the tests from each service directory:

```powershell
cd application-service
mvn test

cd ..\user-service
mvn test
```

The controller test suites have passed locally: 18 tests in total.

## Security Note

The default database credentials in the Compose configuration are for local development only. Override them for your own environment and never commit real passwords, API keys, or other secrets.

## Future Improvements

- Authentication and authorization
- Pagination and filtering
- CI/CD pipeline
- Centralized logging and monitoring
- API documentation with OpenAPI/Swagger
