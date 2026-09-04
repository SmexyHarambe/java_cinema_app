#!/bin/bash

# ==============================================================================
# UAS Disprog Modern - Ubuntu Server Setup Script
# Target IP: 192.168.74.157 (atau IP Ubuntu VM kamu)
# ==============================================================================

set -e

echo "========================================="
echo " Installing Dependencies for Ubuntu VM"
echo "========================================="

# 1. Update Package List
sudo apt-get update -y

# 2. Install OpenJDK 21
echo "[1/6] Installing OpenJDK 21..."
sudo apt-get install -y openjdk-21-jdk

# 3. Install Maven
echo "[2/6] Installing Maven..."
sudo apt-get install -y maven

# 4. Install Node.js 18 & npm
echo "[3/6] Installing Node.js 18 & npm..."
curl -fsSL https://deb.nodesource.com/setup_18.x | sudo -E bash -
sudo apt-get install -y nodejs

# 5. Install MySQL Server
echo "[4/6] Installing MySQL Server..."
sudo apt-get install -y mysql-server
sudo systemctl enable mysql
sudo systemctl start mysql

# Setup MySQL Database
echo "[5/6] Setting up MySQL Database..."
sudo mysql -e "CREATE DATABASE IF NOT EXISTS uasdisprog;"
sudo mysql -e "ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY '';" || true
sudo mysql -e "FLUSH PRIVILEGES;"

# 6. Install Git & Curl
echo "[6/6] Installing Git & Curl..."
sudo apt-get install -y git curl net-tools

echo ""
echo "========================================="
echo " All Dependencies Installed Successfully!"
echo "========================================="
echo "Java Version:"
java -version
echo ""
echo "Maven Version:"
mvn -version
echo ""
echo "Node Version:"
node -v
echo ""
echo "MySQL Status:"
sudo systemctl status mysql --no-pager | head -n 5
echo "========================================="
