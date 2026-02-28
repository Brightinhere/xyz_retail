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

### Important: Database migrations

The backend currently has Flyway disabled, so you need to run SQL migrations manually after containers are up.

#### Option A — run from your host machine (requires `psql`)

```bash
psql -h localhost -U not_root_user -d xyz_retail -f backend/src/main/resources/db/migration/V1__init_schema.sql
psql -h localhost -U not_root_user -d xyz_retail -f backend/src/main/resources/db/migration/V2__seed_products_inventory.sql
```

#### Option B — run inside the PostgreSQL container

```bash
docker cp backend/src/main/resources/db/migration/V1__init_schema.sql xyz_retail_db:/tmp/V1__init_schema.sql
docker cp backend/src/main/resources/db/migration/V2__seed_products_inventory.sql xyz_retail_db:/tmp/V2__seed_products_inventory.sql
docker exec -it xyz_retail_db psql -U not_root_user -d xyz_retail -f /tmp/V1__init_schema.sql
docker exec -it xyz_retail_db psql -U not_root_user -d xyz_retail -f /tmp/V2__seed_products_inventory.sql
```

After migrations, open:

- `http://localhost:5000`

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
