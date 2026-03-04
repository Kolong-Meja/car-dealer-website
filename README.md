# Car Dealer Website

A containerized full-stack web application for managing car dealership workflows. The project combines a **Spring Boot backend** with a **Next.js frontend**, orchestrated using **Docker Compose** for consistent development and deployment environments.

The repository demonstrates a modern full-stack architecture where backend services, frontend applications, and supporting infrastructure are developed and deployed together. The goal is to provide a clean reference for building scalable web platforms using Java, modern React frameworks, and containerized infrastructure.

The backend is implemented using **Spring Boot 4.0.2**, providing REST APIs and service layers. The frontend uses **Next.js 16.1.6**, enabling server-side rendering, optimized builds, and modern React development patterns. All components run inside Docker containers defined in [`compose.yml`](./compose.yml), allowing the system to be reproduced reliably across environments.

---

# Techniques Used

This repository highlights several techniques commonly used in modern production web applications.

### Containerized Multi-Service Architecture
The project uses **Docker Compose** to orchestrate multiple services. The configuration in [`compose.yml`](./compose.yml) defines the runtime environment for backend, frontend, and supporting services.

This approach allows developers to run the entire system locally using the same environment used in deployment.

Relevant documentation:

- https://docs.docker.com/compose/

---

### Multi-Stage Docker Builds
Both backend and frontend containers use **multi-stage Docker builds** to keep production images small and reproducible. Dependency installation and compilation happen in build stages, while runtime images only include the minimal artifacts needed to run the application.

Multi-stage builds are described here:

- https://docs.docker.com/build/building/multi-stage/

---

### Server-Side Rendering with Next.js
The frontend is built with **Next.js**, which enables server-side rendering and optimized client delivery. Rendering logic can run on the server to improve performance and SEO.

This project relies on Next.js runtime features such as routing, server components, and optimized asset delivery.

Next.js documentation:

- https://nextjs.org/docs

---

### Environment Configuration
Runtime configuration is managed using environment variables defined in [`.env`](./.env). Docker Compose injects these variables into containers at runtime.

This technique keeps secrets and environment-specific configuration outside of application code.

Related documentation:

- https://docs.docker.com/compose/environment-variables/

---

### REST API Architecture
The backend service exposes a RESTful API implemented with Spring Boot. Controllers define HTTP endpoints, services encapsulate business logic, and persistence layers interact with the database.

REST fundamentals are documented here:

- https://developer.mozilla.org/en-US/docs/Glossary/REST

---

# Technologies and Libraries

The following tools and frameworks are central to this project.

### Spring Boot
Backend application framework.

- https://spring.io/projects/spring-boot

Version used:

- Spring Boot **4.0.2**

Spring Boot provides:

- dependency injection
- embedded server
- production-ready REST APIs

---

### Next.js
React framework used for the frontend application.

- https://nextjs.org/

Version used:

- Next.js **16.1.6**

Key capabilities include:

- server components
- file-system routing
- optimized bundling
- hybrid rendering strategies

---

### Docker
Container platform used to build and run services.

- https://www.docker.com/

The repository includes:

- [`compose.yml`](./compose.yml)
- [`docker-bake.hcl`](./docker-bake.hcl)

These files define container builds and runtime orchestration.

---

### Docker Bake
The build pipeline uses **Docker Bake** to define image build targets and build configurations in [`docker-bake.hcl`](./docker-bake.hcl).

Docker Bake documentation:

- https://docs.docker.com/build/bake/

This approach allows multiple container images to be built using a single configuration.

---

### PostgreSQL
Primary relational database used by the backend.

- https://www.postgresql.org/

---

### Redis
Redis is commonly used in containerized Spring Boot stacks for caching, rate limiting, or session storage.

- https://redis.io/

---

# Project Structure

```
.
├── config
│
├── www
│   ├── cardealer
│   └── next-frontend
│
├── .env
├── .gitignore
├── compose.yml
├── docker-bake.hcl
├── LICENSE
└── README.md
```

### `config/`
Configuration files for supporting infrastructure such as database administration tools or container runtime configuration.

---

### `www/`
Contains application source code.

#### `www/cardealer/`
Spring Boot backend application implementing the API layer, business logic, and persistence.

#### `www/next-frontend/`
Next.js frontend application responsible for rendering the user interface and interacting with the backend API.

---

### `.env`
Environment variable definitions used by Docker Compose to configure container runtime settings.

---

### `compose.yml`
Defines the multi-container environment used during development and deployment.

---

### `docker-bake.hcl`
Configuration file used by Docker Bake to define image build targets and build strategies.

---

### `LICENSE`
Open source license applied to the project.