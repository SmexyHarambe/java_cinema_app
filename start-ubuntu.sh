#!/bin/bash

# ==============================================================================
# UAS Disprog Modern - Start Script for Ubuntu
# Jalankan dari folder root project (yang berisi backend/, tcp-server/, frontend/)
# ==============================================================================

set -e

echo "========================================="
echo " Starting UAS Disprog Services"
echo "========================================="

# Cek apakah project ada
if [ ! -d "backend" ] || [ ! -d "tcp-server" ] || [ ! -d "frontend" ]; then
    echo "ERROR: Project files not found in current directory!"
    echo "Please run this script from the project root."
    exit 1
fi

# Muat env lokal (TIDAK di-commit, lihat .env.example) bila ada.
# Contoh isi: TCP_ENCRYPTION_ENABLED=true + TCP_ENCRYPTION_KEY_BASE64=...
if [ -f "./.env.local" ]; then
    set -a
    # shellcheck disable=SC1091
    . ./.env.local
    set +a
    echo "Loaded ./.env.local"
fi

# 0. Matikan proses lama dulu agar tidak konflik port 8080 / 6002.
#    (Dulu script tidak melakukan ini sehingga backend baru gagal start
#    dengan "Port 8080 was already in use" dan yang jalan tetap kode lama.)
echo "[0/4] Stopping old services (if any)..."
pkill -f "backend-2.0.0.jar" 2>/dev/null || true
pkill -f "tcp-server-2.0.0.jar" 2>/dev/null || true
sleep 3

# 1. Start MySQL
echo "[1/4] Starting MySQL..."
sudo systemctl start mysql
sleep 2

# 2. Build & Start Backend (port 8080)
#    setsid + nohup agar proses tetap hidup walau terminal di-Ctrl+C.
echo "[2/4] Building and Starting Backend..."
cd backend
mvn clean package -DskipTests
setsid nohup java -jar target/backend-2.0.0.jar > ../backend.log 2>&1 < /dev/null &
echo $! > ../backend.pid
cd ..
echo "Backend started (PID $(cat backend.pid), log: backend.log)"
sleep 15

# 3. Build & Start TCP Server (port 6002)
echo "[3/4] Building and Starting TCP Server..."
cd tcp-server
mvn clean package -DskipTests
setsid nohup java -jar target/tcp-server-2.0.0.jar > ../tcp-server.log 2>&1 < /dev/null &
echo $! > ../tcp-server.pid
cd ..
echo "TCP Server started (PID $(cat tcp-server.pid), log: tcp-server.log)"

# 4. Frontend dijalankan manual di terminal lain
echo "[4/4] Frontend: jalankan di terminal lain:"
echo "  cd frontend && npm install && npm run dev -- --host"

echo ""
echo "========================================="
echo " UAS Disprog Services Started!"
echo "========================================="
echo ""
echo "Access URLs (dari laptop, ganti dengan IP VM bila perlu):"
echo "  - Frontend:    http://localhost:3000"
echo "  - Backend API: http://localhost:8080"
echo "  - TCP Server:  localhost:6002"
echo "  - MySQL:       localhost:3306"
echo ""
echo "Logs: backend.log, tcp-server.log"
echo "Stop: ./stop-ubuntu.sh"
echo "========================================="
