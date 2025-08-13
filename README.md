# AnySpotsLeft
A Spring Boot web application

**run and test the app locally on **http://localhost:8080****

---

### Key user flows
- Public: Home (`/`), Discover (`/discover`)
- Auth: Login (`/login`), Signup (`/signup`, `/signup/host`)
- Host: Host dashboard (`/host`), Create listing (`POST /listings/create`), Remove listing (`POST /listings/remove`)
- Listings: Join via Stripe Checkout (`POST /listings/join/{listingId}`) → success (`/checkout/success`)
- Parking spots: Apply (`POST /parkingSpot/create`), Verify (ADMIN) (`POST /parkingSpot/verify`)
- Profile: View (`/profile/{userId}`), Update vehicle & image, change email/password

---

## Tech Stack
- **Java:** 21
- **Spring Boot:** 3.5.3
- **Modules:** Web, Thymeleaf, Security, Data JPA, Mail (present), DevTools
- **DB:** PostgreSQL (via Docker)
- **Payments:** Stripe (stripe-java 29.4.0)
- **Build:** Maven (via Spring Boot plugin)
- **Lombok:** used for DTOs/entities

---

## Prerequisites
- **Java 21** 
- **Docker** (for Postgres database)
- **Maven 3.9+** use a Maven Wrapper

> Add **Maven Wrapper** once to run without installing Maven:
> ```bash
> mvn -N wrapper
> git add mvnw mvnw.cmd .mvn/wrapper
> git commit -m "add Maven Wrapper"
> ```

---

## Starting the proccess

### 1) Start PostgreSQL
Using the included **docker-compose.yml**:
```bash
docker compose up -d
```
This will launch Postgres on **localhost:5432** with database **anyspotsleft** and user/password **postgres/postgres**.

> If want to use your own Postgres, match the database name/user/pass or update Spring config below.

### 2) Configure the app (no secrets in Git)
On the local configuration create _application.yml_ file or _application.properties_ file.
this will be used to set env vars instead of hardcoding values (see **Environment**).

- Stripe keys are obtainable from the Stripe dashboard search when searched **"api" or "key"**

- Google Maps API key can be obtained on their platform. The following link explains the proccess:

https://developers.google.com/maps/documentation/javascript/get-api-key



### 3) Run
With **Maven Wrapper**:
```bash
./mvnw spring-boot:run
# Windows: mvnw.cmd spring-boot:run
```

With Maven installed:
```bash
mvn spring-boot:run
```

### 4) Open
- App: http://localhost:8080
- Discover page: http://localhost:8080/discover

---

## Configuration File

Spring uses `src/main/resources/application.yml` (or `.properties`). Edit this config to fit your local setup.

`src/main/resources/application-example.yml`:
```yaml
server:
  port: 8080

spring:
  datasource:
    url: ${SPRING_DATASOURCE_URL:jdbc:postgresql://localhost:5432/anyspotsleft}
    username: ${SPRING_DATASOURCE_USERNAME:postgres}
    password: ${SPRING_DATASOURCE_PASSWORD:postgres}
    driver-class-name: org.postgresql.Driver
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        jdbc:
          lob:
            non_contextual_creation: true

 Optional integrations
stripe:
  publicKey: ${STRIPE_PUBLIC_KEY:}
  secretKey: ${STRIPE_SECRET_KEY:}

google:
  maps:
    apiKey: ${GOOGLE_MAPS_API_KEY:}
```

**Environment variables** `.env`

This will be used to import keys onto the application.
```bash
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/anyspotsleft
export SPRING_DATASOURCE_USERNAME=postgres
export SPRING_DATASOURCE_PASSWORD=postgres

# Optional
export STRIPE_PUBLIC_KEY=
export STRIPE_SECRET_KEY=
export GOOGLE_MAPS_API_KEY=
```

---

## Database (Docker Compose)
`docker-compose.yml` (also included in the github files):
```yaml
services:
  db:
    image: postgres:16
    container_name: anyspotsleft-db
    ports:
      - "5432:5432"
    environment:
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
      POSTGRES_DB: anyspotsleft
    volumes:
      - pgdata:/var/lib/postgresql/data
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U postgres"]
      interval: 5s
      timeout: 5s
      retries: 5
volumes:
  pgdata:
```

**To stop / clean DB:**
```bash
docker compose down -v
```

---

## Project Structure 
```
src/main/java/com/anyspotsleft/anyspotsleft/...   # Controllers, services, entities, security
src/main/resources/templates                      # Thymeleaf
src/main/resources/static                         # CSS, JS, images
src/main/resources/application-example.yml        # copy to application.yml locally)
pom.xml
docker-compose.yml
README.md
```

---

## Common issue troubleshooting:

- **Port in use (8080 or 5432)**  
  Change app: `server.port=8081`  
  Change DB: map `5433:5432` and update JDBC URL.
- **Cannot connect to DB**  
  `docker ps` 
- **Maven not recognizing dependencies**  
  `./mvnw clean spring-boot:run`

---

---

## quick path to start
1. `docker compose up -d`
2. `./mvnw spring-boot:run`
3. Open http://localhost:8080
4. Sign up (user or host), log in, create a spot, create a listing, join a listing (Stripe optional if keys are provided).

---
