#!/bin/bash

# ==============================================================================
# UAS Disprog Modern - Stop Script for Ubuntu
# ==============================================================================

echo "========================================="
echo " Stopping UAS Disprog Services"
echo "========================================="

# Via PID file (dibuat oleh start-ubuntu.sh)
if [ -f "backend.pid" ]; then
    if kill "$(cat backend.pid)" 2>/dev/null; then
        echo "Backend stopped."
    else
        echo "Backend not running."
    fi
    rm -f backend.pid
fi

if [ -f "tcp-server.pid" ]; then
    if kill "$(cat tcp-server.pid)" 2>/dev/null; then
        echo "TCP Server stopped."
    else
        echo "TCP Server not running."
    fi
    rm -f tcp-server.pid
fi

# Fallback: bunuh berdasarkan nama jar.
# (Pola lama "UASDisprogBackendApplication" tidak pernah cocok karena
#  command line proses adalah "java -jar target/...jar".)
pkill -f "backend-2.0.0.jar" 2>/dev/null || true
pkill -f "tcp-server-2.0.0.jar" 2>/dev/null || true

echo ""
echo "Services stopped."
echo "========================================="
