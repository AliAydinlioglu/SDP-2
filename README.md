# SDP II - Delaware Project

This repository contains the full multi-tier software project developed for the Software Development Project II (SDP II) course.

---

## 🏛️ Project Architecture

```
                       ┌────────────────────────┐
                       │  React Web Application │
                       │    (Port 5173)         │
                       └───────────┬────────────┘
                                   │ HTTP / REST
                                   ▼
                       ┌────────────────────────┐
                       │  Node.js / Koa Backend │
                       │    (Port 9000)         │
                       └───────────┬────────────┘
                                   │ Prisma ORM
                                   ▼
┌────────────────────────┐         ┌────────────────────────┐
│  JavaFX Desktop App    │ ──JDBC─▶│   MySQL Database       │
│  (sdp2-java)           │         │   (Port 3307 internal:3306)│
└────────────────────────┘         └────────────────────────┘
```

1. **[2025-react-gent13](./2025-react-gent13)**: Frontend web application built with React 19, Vite, React Router, Recharts, and React Bootstrap.
2. **[2025-nodejs-gent13](./2025-nodejs-gent13)**: REST API backend built with Node.js, Koa, TypeScript, Prisma ORM, and Argon2 password hashing.
3. **[2025-java-gent13](./2025-java-gent13)**: Desktop management client built with Java 21, JavaFX, and JPA / EclipseLink.
4. **MySQL Database**: Central database storing users, sites, machines, maintenance records, and notifications.

---

## 🚀 Running with Docker / Podman (Zero Local Installation)

Run the entire application stack in isolated containers without installing Node, Yarn, Java, or MySQL on your host system:

```bash
docker compose up -d
# or using podman-compose:
podman-compose up -d
```

### Accessing the Applications

| Component | URL | Description |
|---|---|---|
| **Frontend** | [http://localhost:5173](http://localhost:5173) | Web Dashboard and Management UI |
| **Backend API** | [http://localhost:9000](http://localhost:9000) | REST API |
| **Health Ping** | [http://localhost:9000/api/health/ping](http://localhost:9000/api/health/ping) | Backend Health Check |
| **MySQL DB** | `localhost:3307` | Database (`root` / `root`, DB: `Local_SDP2`) |

*(Note: MySQL is exposed on host port `3307` by default to avoid conflicts if another MySQL instance is already running on port 3306).*

### Stopping the Stack

```bash
docker compose down
# or
podman-compose down
```

---

## 🔑 Default Seeded Accounts

The database is automatically initialized and seeded on first startup with the following test credentials:

| Email | Password | Role |
|---|---|---|
| `geralt@gmail.com` | `12345678` | **Administrator** |
| `triss@gmail.com` | `12345678` | **Technieker** |
| `yennefer@gmail.com` | `12345678` | **Gebruiker** |

---

## 💻 Cross-Platform Compatibility (Linux & Windows)

- **Line Endings**: `.gitattributes` files are configured across all sub-repositories to enforce LF line endings and prevent CRLF conversion issues.
- **Path Separators**: Paths in `.gitignore` and configuration files use standard forward slashes (`/`).
- **Container Networking**: Vite and Koa are configured to bind to `0.0.0.0` so they are accessible from both host and Docker networks.
- **MySQL Case Sensitivity**: Configured with `--lower_case_table_names=1` to ensure database tables behave consistently on both Linux and Windows.
