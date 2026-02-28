# XYZ Retail

General project README for the full-stack XYZ Retail assignment.

## Live Demo

You can view the hosted project directly in your browser:

- **https://xyz.irfanbilir.nl**

## Project Modules

This repository contains:

- `backend/` — Java 21 + Spring Boot REST API
- `frontend/` — Vue 3 + Vite single-page application

For module-specific details, see:

- [Backend README](backend/README.md)
- [Frontend README](frontend/README.md)

## Run Locally (Docker Compose)

From the repository root (this folder):

```bash
docker compose up -d --build
```

This starts:

- PostgreSQL on `localhost:5432`
- Backend API on `http://localhost:8090`
- Frontend on `http://localhost:5000`

## Run Locally (Without Docker)

Use each module’s own instructions:

- [Backend run instructions](backend/README.md#how-to-run)
- [Frontend run instructions](frontend/README.md#run-locally)

## Stop the Stack

```bash
docker compose down
```

To also remove PostgreSQL persisted data volume:

```bash
docker compose down -v
```

## Coverage report

Latest report of the backend's coverage

<img width="959" height="271" alt="image" src="https://github.com/user-attachments/assets/ad9c2f5c-8f71-4f0f-a30a-d56cf96eecc7" />

