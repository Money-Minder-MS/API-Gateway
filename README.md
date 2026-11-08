# Money Minder MS — API Gateway & System Overview

Spring Cloud Gateway for **Money Minder MS**, a microservices platform for
personal-finance management (expenses, revenue, savings objectives, complaints,
and a community forum). This gateway is the single entry point: it routes
requests to the services, secures them as an OAuth2 resource server (Keycloak),
and aggregates every service's Swagger UI.

## Architecture

```
                        ┌────────────────┐
  Angular frontend ───► │  API-Gateway   │ ─┐  OAuth2 (Keycloak realm: money-minder)
      (:4200)           │    (:9000)     │  │  aggregated Swagger UI
                        └────────┬───────┘  │
                                 │ routes   │
        ┌────────────────────────┼──────────┴─────────────────────┐
        ▼            ▼           ▼            ▼          ▼          ▼
   Utilisateur   Depense    Revenue     Objectifs  Reclamations  Forum
     (:8088)     (:8081)    (:8090)      (:8087)     (:8082)     (:8083)
        └────────────┴───────────┴────────────┴──────────┴─────────┘
                                 │  events (RabbitMQ)
                                 ▼
                          NotificationMS (Django)

   Service discovery: EurekaServer (:8761)   Config: Config-Server (:8099)
```

## Services

| Service | Port | Role |
| --- | --- | --- |
| [EurekaServer](https://github.com/Money-Minder-MS/EurekaServer) | 8761 | Service discovery |
| [Config-Server](https://github.com/Money-Minder-MS/Config-Server) | 8099 | Centralized configuration |
| API-Gateway | 9000 | Routing, OAuth2 security, aggregated Swagger |
| [Gestion-Utilisateur](https://github.com/Money-Minder-MS/Gestion-Utilisateur) | 8088 | Users & roles (Keycloak) |
| [depense](https://github.com/Money-Minder-MS/depense) | 8081 | Expense management |
| [gestion_revenue](https://github.com/Money-Minder-MS/gestion_revenue) | 8090 | Revenue & categories |
| [Gestion-Objectifs-](https://github.com/Money-Minder-MS/Gestion-Objectifs-) | 8087 | Savings objectives |
| [Reclamations](https://github.com/Money-Minder-MS/Reclamations) | 8082 | Complaints |
| [gestion-forum](https://github.com/Money-Minder-MS/gestion-forum) | 8083 | Community forum |
| [NotificationMS](https://github.com/Money-Minder-MS/NotificationMS) | — | RabbitMQ-driven notifications (Django) |
| [Frontend-Money_Minder](https://github.com/Money-Minder-MS/Frontend-Money_Minder) | 4200 | Angular user interface |

## Tech stack

Java · Spring Boot · Spring Cloud Gateway · Spring Security (OAuth2 resource
server) · Eureka client · springdoc-openapi (WebFlux)

## Run

```bash
mvn spring-boot:run
```

- Gateway: http://localhost:9000
- Aggregated Swagger UI: http://localhost:9000/swagger-ui.html
- Port: `9000`

Authentication is delegated to Keycloak (realm `money-minder`). The JWK set URI
is configurable via `SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_JWK_SET_URI`.

## Running the whole system

1. Start infrastructure: **EurekaServer** (8761), then **Config-Server** (8099).
2. Start Keycloak and RabbitMQ (for auth and notifications).
3. Start the business services (any order; they register with Eureka).
4. Start **API-Gateway** (9000) and the Angular **frontend** (4200).
