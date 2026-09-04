# 🎬 Java Cinema App — Ticket Booking Platform

A full-stack cinema ticket booking platform: **Spring Boot 3 REST API** +
**TCP notification server** + **React frontend** + **MySQL**, running on
**Java 21**. Modernized from a university distributed-programming assignment
that was originally NetBeans/Ant + SOAP + Java Swing on Java 8.

## ✨ What the app does

**Customer**
- Register & login (BCrypt passwords, JWT, 24 h token lifetime)
- Browse movies, view details, pick seats on a seat map
- Pay with in-app balance (top-up anytime), view transaction history
- Cancel bookings up to 7 days before showtime (automatic refund)
- Receive real-time notifications (booking success, top-up success)

**Admin**
- CRUD movie tickets + poster image upload + flash-sale flag
- View all customer transactions

## 🏗️ Architecture & deployment topology

The system is split across two machines (also runnable all-in-one):

```
┌─ Windows host (192.168.74.1) ───────────────────┐
│  Browser ──► React (Vite dev, :3000)            │
│  TCP Notification Server (:6002, plaintext/TLS) │
│  Wireshark on VMnet8 (traffic monitoring)       │
└──────────────────────┬──────────────────────────┘
                       │ HTTP :8080 ▲ │ ▼ TCP :6002
┌──────────────────────┴──────────────────────────┐
│  Ubuntu VM (192.168.74.157)                     │
│  Spring Boot REST API (:8080)                   │
│  MySQL 8 (:3306)                                │
└─────────────────────────────────────────────────┘
```

### How it works (request flows)

1. **Auth** — `POST /api/auth/login` verifies BCrypt hash → returns JWT
   (`userId`, `username`, `role`). Frontend stores it (Zustand) and sends it
   as `Bearer` on every request. Spring Security enforces
   `CUSTOMER`/`ADMIN` roles per endpoint.
2. **Booking** — `POST /api/customer/transactions` checks balance → deducts
   balance → decrements seat stock → inserts `transaction` row → opens a
   **TCP socket to port 6002** (`<pesan>-personal-<customerId>`) → TCP server
   persists the notification (`notifikasis` + `notif_users`) → frontend shows
   it under the bell icon (`is_read` tracked per user).
3. **Top-up / cancel** — same pattern: balance update + TCP notification
   (cancel = refund, only allowed ≥ 7 days before showtime).

### TCP notification protocol (port 6002, newline-delimited)

- `{message}-broadcast` — broadcast to all users
- `{message}-personal-{customerId}` — personal notification
- `none-hitungnotif-{customerId}-0` — server replies `jumlahBelumBaca-N`
- `none-bacanotif-{customerId}-0` — server replies `notifContent-<json>`
- `none-hapusnotif-{notifId}-{customerId}` — delete notification
- Optional **TLS mode** (`tcp.tls.enabled`, TLSv1.3/1.2, self-signed cert via
  `scripts/gen-tls-certs.sh`): same line protocol, transport encrypted.
  Wireshark then shows `ClientHello … Application Data` instead of plaintext.

### Traffic monitoring (assignment evidence)

- Capture on **VMnet8**: `tcp.port == 6002` (notifications), `tcp.port == 8080`
  (REST), `tcp.port == 3306` (MySQL), or `ip.addr == 192.168.74.157` (all).
- One booking produces the full chain on the wire: `HTTP :8080`
  → `TCP :6002` → `MySQL :3306`.
- Plaintext vs TLS runs are directly comparable captures. (Note: browser →
  Vite traffic stays on laptop loopback and is invisible to Wireshark; the
  real API traffic laptop → VM is what gets captured.)

## 🛠️ Tech stack

| Layer | Tech |
|---|---|
| Language | Java 21 (LTS) |
| Backend | Spring Boot 3.3, Spring Data JPA/Hibernate, Spring Security + JWT, Spring WebSocket, HikariCP, Maven |
| Realtime | Raw TCP sockets (:6002), optional TLS |
| Frontend | React 18, Vite, React Router v6, Tailwind CSS, Zustand, Axios |
| Database | MySQL 8 (JPA auto-DDL + idempotent seeder that also repairs legacy plain-text passwords) |
| DevOps | Dockerfiles + `docker-compose`, Ubuntu/Windows setup scripts |

## 📁 Project structure

```
├── backend/        # Spring Boot REST API (entity / repository / service /
│                   #   controller / security / dto / config)
├── tcp-server/     # Standalone TCP notification server (port 6002)
├── frontend/       # React SPA (pages / services / store)
├── scripts/        # gen-tls-certs.sh (self-signed certs via keytool)
├── docker/         # docker-compose for one-shot deployment
├── *.sh / *.ps1    # install-deps / start / stop helpers (root)
└── QUICKSTART.md   # detailed run guide
```

## 🚀 Getting started

Prereqs: JDK 21, Maven, Node.js 18+, MySQL 8.

**All-in-one (single machine):**
```bash
mysql -u root -e "CREATE DATABASE IF NOT EXISTS uasdisprog;"
cd backend && mvn clean package -DskipTests && java -jar target/*.jar &
cd ../tcp-server && mvn clean package -DskipTests && java -jar target/*.jar &
cd ../frontend && npm install && npm run dev
# frontend http://localhost:3000 · api http://localhost:8080 · tcp localhost:6002
```

**Split (VM + host, as diagrammed):** on the Ubuntu VM run `./install-deps-ubuntu.sh`
once, then `./start-ubuntu.sh`. Point the TCP server at the DB via env vars
(`SPRING_DATASOURCE_URL/USERNAME/PASSWORD`), point the backend at the TCP host
(`TCP_SERVER_HOST=<host-ip>` in `.env.local`), enable TLS with
`TCP_TLS_ENABLED` + keystore/truststore paths (see `QUICKSTART.md` and
`.env.example`). Frontend proxy target → `http://<vm-ip>:8080`.

**Demo accounts** (seeded automatically): `Fi`/`1234`, `Ab`/`5678`,
`Dan`/`abcd` (balance 1000), admin `b`/`c`.

## 🔄 Modernization notes (from v1.0 NetBeans project)

SOAP/JAX-WS → REST · Swing → React · Ant → Maven · raw JDBC → Spring Data JPA
· Java 8 → 21 · plain passwords → BCrypt+JWT · hardcoded config → properties +
env vars · raw TCP kept for the monitoring assignment, with optional TLS added.

## 📄 License

Educational use only (university assignment project).
