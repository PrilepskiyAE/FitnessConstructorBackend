# Multi-Container Application: Web Client + Kotlin Server + MongoDB + Mongo Express

This application is deployed using Docker Compose and includes 4 services:
* **web-client** — static web interface powered by Nginx;
* **mongodb** — MongoDB 6.0 database;
* **mongo-express** — web GUI for MongoDB management;
* **kotlin-server** — backend service written in Kotlin.
---
### Prerequisites

* Docker Engine 20.10 or later;
* Docker Compose (v2 or v1);
* basic command-line knowledge.
---
### Project structure
.\
├── app/ # Kotlin server source code (Dockerfile inside) \
├── mongo/ \
│ ├── mongodb_data/ # Volume for MongoDB data storage \
│ └── init-mongo.js # DB initialization script \
├── nginx/ \
│ └── conf.d/ \
│ └── default.conf # Nginx configuration \
├── web-client/ # Static web interface files \
├── docker-compose.yml # Docker Compose configuration \
└── README.md
---
### Environment variables setup

It’s recommended to create a `.env` file in the project root to override default values:

MONGO_ADMIN_USER=my_admin \
MONGO_ADMIN_PASS=StrongPassword456! \
MONGO_APP_DB=application_db \
MONGO_APP_USER=app_user \
MONGO_APP_PASS=AppUserPass789! \
ME_USER=expressuser \
ME_PASS=expresspass123 

If the `.env` file isn’t created, default values from docker-compose.yml will be used.
---
### Starting the application

1. Create a `.env` file (optional, see above) or use default values.
2. Build and start containers: `docker-compose up -d`
3. Check container status: `docker-compose ps`
All services should have the `Up` status (and `mongodb` should pass the health check).
--- 
### Accessing services
After successful startup, the following endpoints are available:

#### Web interface (web-client): http://servername:80  

#### Mongo Express (MongoDB GUI): http://servername:8081

 - Login: ${ME_USER} (default: expressuser)

 - Password: ${ME_PASS} (default: expresspass123)

 -  Kotlin server:  http://servername:80/app/.
--- 

## Service descriptions
### web-client (Nginx)
- Image: nginx:alpine

- Exposed port: 80:80

### Volumes:

- ./web-client:/usr/share/nginx/html — static website files;

- ./nginx/conf.d/default.conf:/etc/nginx/conf.d/default.conf — custom Nginx configuration.

#### Network: app-network

#### Restart policy: unless-stopped

###  mongodb (MongoDB 6.0)
- Image: mongo:6.0

- Ports: not exposed externally (access only within the Docker network).

### Volumes:

- ./mongo/mongodb_data:/data/db — persistent data storage;

- ./mongo/init-mongo.js:/docker-entrypoint-initdb.d/init-mongo.js — initialization script (creates the app user and DB on first run).

## Environment variables:

MONGO_INITDB_ROOT_USERNAME — DB admin user;

MONGO_INITDB_ROOT_PASSWORD — admin password;

MONGO_INITDB_DATABASE — app database name.

#### Healthcheck: verifies DB availability via mongosh.

#### Network: app-network

### mongo-express
- Image: mongo-express

- Exposed port: 8081:8081

- Dependency: waits for mongodb to become healthy.

####  Environment variables:

ME_CONFIG_MONGODB_SERVER — MongoDB service hostname (mongodb);

ME_CONFIG_MONGODB_ADMINUSERNAME / ME_CONFIG_MONGODB_ADMINPASSWORD — MongoDB connection credentials;

ME_CONFIG_BASICAUTH_USERNAME / ME_CONFIG_BASICAUTH_PASSWORD — login and password for the Mongo Express web interface.

#### Network: app-network

## kotlin-server
- Build: from the ./app context using Dockerfile.

- Ports: not exposed externally (internal service).

### Environment variables:

MONGODB_HOST / MONGODB_PORT — MongoDB address;

MONGODB_DATABASE / MONGODB_USERNAME / MONGODB_PASSWORD — DB connection parameters;

SERVER_PORT — server port inside the container (8080).

#### Dependency: waits for mongodb to become healthy.

#### Network: app-network

### Networks and volumes
Network app-network (driver: bridge) — enables network communication between containers.

Volume mongodb_data — persistent storage for MongoDB data (declared in the volumes: section of docker-compose.yml).
Useful commands


### Stop and remove containers:

```bash
docker-compose down
```

### View service logs (e.g., for mongodb):

```bash
docker-compose logs mongodb
```

### Run a command in a running container (e.g., access the app shell):

``` bash
docker-compose exec app sh
```

### Rebuild the kotlin-server image and restart:

``` bash
docker-compose up --build -d app
```
---
## Advantages of Microservice Architecture (Docker Compose Setup) over Monolithic Architecture

#### 1. Service Isolation and Fault Tolerance

In a monolith, a single component failure can crash the entire application. The microservice approach provides:

* each service (`web-client`, `mongodb`, `mongo-express`, `kotlin-server`) runs in an isolated container;
* failure of one service doesn’t affect others;
* independent restart, update, or scaling of any service without impacting the rest.

#### 2. Technology Stack Flexibility

Monoliths are typically bound to a single tech stack. Microservices allow:

* `web-client` to use Nginx for static content delivery;
* `kotlin-server` to be written in Kotlin (or any other language);
* `mongodb` as a NoSQL database;
* `mongo-express` as a ready‑made web UI for DB management.

This enables choosing the best tool for each specific task.

#### 3. Simplified Scaling

Monolithic scaling requires duplicating the entire app even if only one component is under load. The microservice setup allows:

* horizontal scaling of only the overloaded services (e.g., `kotlin-server`);
* keeping the database (`mongodb`) separate and avoiding unnecessary duplication;
* efficient caching and distribution of static content (`web-client`).

#### 4. Independent Deployment and Updates

In monoliths, any change requires a full application restart. With microservices:

* updating `web-client` doesn’t affect the backend or database;
* new versions of individual services can be tested without stopping the whole system;
* rollbacks are limited to the specific service being reverted.

#### 5. Dependency and Version Management

In monoliths, library updates can break unrelated modules. Microservices solve this by:

* giving each container its own dependencies (e.g., Alpine in Nginx, specific MongoDB version);
* allowing different library versions across services without conflicts.

#### 6. Resource Optimization

Monoliths consume resources even for unused features. The microservice architecture enables:

* starting services only when needed (via `depends_on` with `service_healthy` condition);
* resource usage proportional to actual load;
* setting different CPU/RAM limits for each service.

#### 7. Streamlined Development and Team Collaboration

Monoliths often lead to code conflicts when multiple developers work on the same codebase. Microservices improve this by:

* enabling parallel development on different services;
* clear division of responsibilities (frontend, backend, database);
* simplified local development using `docker-compose` (all dependencies are containerized).

#### 8. Reliability and Fault Isolation

A monolith is a single point of failure. The microservice design enhances reliability through:

* built‑in `healthcheck` for MongoDB to monitor availability;
* automatic restart policy (`restart: unless-stopped`) for service recovery;
* network isolation via `app-network` to prevent cascading failures.

#### 9. Easier Monitoring and Logging

In monoliths, identifying the source of issues is complex. Microservices offer:

* separate logs for each service (via `docker-compose logs <service>`);
* customizable monitoring per component;
* straightforward identification of bottlenecks (e.g., database load via `mongo-express`).

#### 10. Infrastructure Flexibility

Monoliths are tightly coupled to their environment. Microservices provide:

* seamless migration between environments (dev → test → production);
* support for CI/CD pipelines with separate stages for frontend and backend;
* adaptability to orchestration tools (Kubernetes, Nomad) without rewriting core logic.

---

### Key Advantages from the `docker-compose.yml` Configuration

| Component | Microservice Advantage |
|----------|----------------------|
| `web-client` (Nginx) | Fast static content delivery without backend load. |
| `mongodb` | Isolated database with persistent volume (`mongodb_data`), automated healthcheck, and initialization via `init-mongo.js`. |
| `mongo-express` | Dedicated web UI for database administration without affecting the main server. |
| `kotlin-server` | Backend scales independently and uses environment variables for flexible DB connection configuration. |
| `app-network` | Internal network ensures secure inter‑service communication without exposing unnecessary ports. |

---
