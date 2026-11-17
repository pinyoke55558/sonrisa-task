# Architecture Plan

## 1. Context Diagram

```mermaid
flowchart LR
    U["Users<br/>user, admin (browser)"]
    FE["React frontend<br/>single-page app"]
    BE["Spring Boot backend<br/>REST API, matching, notifications"]
    KC["Keycloak<br/>IAM, OpenID Connect"]
    DB[("PostgreSQL<br/>application database")]
    KDB[("PostgreSQL<br/>Keycloak database")]
    SRC["Event source<br/>dummy API caller"]
    SLACK["Slack"]
    SMTP["SMTP server"]

    U -->|uses| FE
    FE -->|"sign on (OIDC)"| KC
    FE -->|"REST + JWT"| BE
    BE -->|validate tokens| KC
    BE -->|JDBC| DB
    KC -->|JDBC| KDB
    SRC -->|dummy API| BE
    BE -->|webhook| SLACK
    BE -->|SMTP| SMTP

    classDef ours fill:#EEEDFE,stroke:#534AB7,color:#26215C
    classDef infra fill:#E1F5EE,stroke:#0F6E56,color:#04342C
    classDef external fill:#F1EFE8,stroke:#5F5E5A,color:#2C2C2A
    class FE,BE ours
    class KC,DB,KDB infra
    class U,SRC,SLACK,SMTP external
```

Purple = built by us · green = infrastructure we run · gray = users and external systems.

| Element | Responsibility |
|---|---|
| **React frontend** | Web application for users and admins. Sends the user to Keycloak to sign on, then calls the backend with the access token (JWT). |
| **Spring Boot backend** | REST API for categories, rules and events; the dummy event API; rule matching; sending notifications. Checks every request's token against Keycloak; never handles passwords. |
| **Keycloak** | External authentication server (IAM): users, sign-on, roles (user, admin). |
| **PostgreSQL – application database** | Categories, rules, events and notification results. Only the backend accesses it. |
| **PostgreSQL – Keycloak database** | Keycloak's own data (users, sessions, settings). A **separate database**, kept apart from the application data. |
| **Event source** | Calls the dummy API to simulate incoming events; stands in for a future real source. |
| **Slack / SMTP server** | Receive notifications from the backend. The frontend never talks to them. |

## 2. Domain Model

## 3. API Design

## 4. Technology
