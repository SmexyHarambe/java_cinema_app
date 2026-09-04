#!/bin/bash

# ==============================================================================
# UAS Disprog Modern - Setup Script for Windows
# Pastikan script dijalankan sebagai Administrator
# ==============================================================================

Write-Host "=========================================" -ForegroundColor Cyan
Write-Host " Installing Dependencies for Windows" -ForegroundColor Cyan
Write-Host "=========================================" -ForegroundColor Cyan

# 1. Install Java 21 via Adoptium
Write-Host "[1/5] Installing OpenJDK 21..." -ForegroundColor Yellow
$javaUrl = "https://api.adoptium.net/v3/binary/latest/21/ga/windows/x64/jdk/hotspot/normal/eclipse?project=jdk"
$javaPath = "$env:TEMP\OpenJDK21.zip"
Invoke-WebRequest -Uri $javaUrl -OutFile $javaPath
Expand-Archive $javaPath -DestinationPath "C:\Program Files\Java"
# Add to PATH
$env:Path += ";C:\Program Files\Java\jdk-21*\bin"
$javaBin = Get-ChildItem "C:\Program Files\Java\jdk-21*" -Recurse -Filter "java.exe" | Select-Object -First 1
if ($javaBin) {
    [Environment]::SetEnvironmentVariable("Path", "$([Environment]::GetEnvironmentVariable('Path', 'Machine'));$($javaBin.Directory)", "Machine")
}

# 2. Install Maven
Write-Host "[2/5] Installing Maven..." -ForegroundColor Yellow
$mavenUrl = "https://dlcdn.apache.org/maven/maven-3/3.9.9/binaries/apache-maven-3.9.9-bin.zip"
$mavenPath = "$env:TEMP\apache-maven.zip"
Invoke-WebRequest -Uri $mavenUrl -OutFile $mavenPath
Expand-Archive $mavenPath -DestinationPath "C:\Program Files\Apache\maven"
[Environment]::SetEnvironmentVariable("Path", "$([Environment]::GetEnvironmentVariable('Path', 'Machine'));C:\Program Files\Apache\maven\bin", "Machine")

# 3. Install Node.js 18
Write-Host "[3/5] Installing Node.js 18..." -ForegroundColor Yellow
$nodeUrl = "https://nodejs.org/dist/v18.20.3/node-v18.20.3-x64.msi"
$nodePath = "$env:TEMP\node-installer.msi"
Invoke-WebRequest -Uri $nodeUrl -OutFile $nodePath
Start-Process msiexec.exe -ArgumentList "/i `"$nodePath`" /qn" -Wait

# 4. Install MySQL
Write-Host "[4/5] Installing MySQL..." -ForegroundColor Yellow
Write-Host "  Please download and install MySQL from:" -ForegroundColor Cyan
Write-Host "  https://dev.mysql.com/downloads/installer/" -ForegroundColor Cyan
Write-Host "  During installation, select:" -ForegroundColor Cyan
Write-Host "  - MySQL Server" -ForegroundColor Cyan
Write-Host "  - Set root password: (kosongkan)" -ForegroundColor Cyan
Write-Host "  After installation, run:" -ForegroundColor Cyan
Write-Host "  mysql -u root -e 'CREATE DATABASE IF NOT EXISTS uasdisprog;'" -ForegroundColor Cyan
Pause

# 5. Install Git
Write-Host "[5/5] Installing Git..." -ForegroundColor Yellow
git --version 2>$null || winget install --id Git.Git -e --source winget

# Setup MySQL Database
Write-Host ""
Write-Host "Setting up MySQL Database..." -ForegroundColor Yellow
mysql -u root -e "CREATE DATABASE IF NOT EXISTS uasdisprog;" 2>$null || Write-Host "  MySQL setup may require manual execution"

Write-Host ""
Write-Host "=========================================" -ForegroundColor Green
Write-Host " Installation Complete!" -ForegroundColor Green
Write-Host "=========================================" -ForegroundColor Green
Write-Host ""
Write-Host "To run the project:" -ForegroundColor Yellow
Write-Host "  1. Build Backend: mvn clean package" -ForegroundColor White
Write-Host "  2. Run Backend: java -jar backend\target\*.jar" -ForegroundColor White
Write-Host "  3. Build TCP Server: mvn clean package" -ForegroundColor White
Write-Host "  4. Run TCP Server: java -jar tcp-server\target\*.jar" -ForegroundColor White
Write-Host "  5. Frontend: cd frontend; npm install; npm run dev" -ForegroundColor White
Write-Host ""
