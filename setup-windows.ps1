#!/bin/bash

# UAS Disprog Setup Script untuk Windows (PowerShell)
# Script ini menginstall semua keperluan untuk menjalankan project UAS Disprog Modern

Write-Host "======================================" -ForegroundColor Cyan
Write-Host "UAS Disprog Modern - Setup Script" -ForegroundColor Cyan
Write-Host "======================================" -ForegroundColor Cyan
Write-Host ""

# Warna
$Red = "Red"
$Green = "Green"
$Yellow = "Yellow"

function Log-Info {
    Write-Host "[INFO] " -ForegroundColor $Green -NoNewline
    Write-Host $args[0]
}

function Log-Warn {
    Write-Host "[WARN] " -ForegroundColor $Yellow -NoNewline
    Write-Host $args[0]
}

function Log-Error {
    Write-Host "[ERROR] " -ForegroundColor $Red -NoNewline
    Write-Host $args[0]
}

# Cek apakah dijalankan sebagai Administrator
$isAdmin = ([Security.Principal.WindowsPrincipal] [Security.Principal.WindowsIdentity]::GetCurrent()).IsInRole([Security.Principal.WindowsBuiltInRole]::Administrator)
if (-not $isAdmin) {
    Log-Warn "Script tidak dijalankan sebagai Administrator. Beberapa perintah mungkin perlu dijalankan dengan Administrator."
}

# Install Java 21
Log-Info "Checking Java 21 installation..."
try {
    $javaVersion = java -version 2>&1 | Select-String "version"
    if ($javaVersion -match "21") {
        Log-Info "Java 21 already installed"
    } else {
        Log-Warn "Java version found but not 21. Installing Java 21..."
        # Download Java 21
        $javaUrl = "https://api.adoptium.net/v3/binary/latest/21/ga/windows/x64/jdk/hotspot/normal/eclipse?project=jdk"
        $javaPath = "$env:TEMP\OpenJDK21-x64.zip"
        Invoke-WebRequest -Uri $javaUrl -OutFile $javaPath
        Expand-Archive $javaPath -DestinationPath "C:\Program Files\Java"
        Log-Info "Java 21 installed"
    }
} catch {
    Log-Info "Java not found. Installing Java 21..."
    $javaUrl = "https://api.adoptium.net/v3/binary/latest/21/ga/windows/x64/jdk/hotspot/normal/eclipse?project=jdk"
    $javaPath = "$env:TEMP\OpenJDK21-x64.zip"
    Invoke-WebRequest -Uri $javaUrl -OutFile $javaPath
    Expand-Archive $javaPath -DestinationPath "C:\Program Files\Java"
    Log-Info "Java 21 installed"
}

# Install Maven
Log-Info "Checking Maven installation..."
try {
    mvn --version | Out-Null
    Log-Info "Maven already installed"
} catch {
    Log-Warn "Maven not found. Installing Maven..."
    # Download Maven
    $mavenUrl = "https://dlcdn.apache.org/maven/maven-3/3.9.9/binaries/apache-maven-3.9.9-bin.zip"
    $mavenPath = "$env:TEMP\apache-maven.zip"
    Invoke-WebRequest -Uri $mavenUrl -OutFile $mavenPath
    Expand-Archive $mavenPath -DestinationPath "C:\Program Files\Apache\maven"
    
    # Add to PATH
    $currentPath = [Environment]::GetEnvironmentVariable("Path", "Machine")
    [Environment]::SetEnvironmentVariable("Path", "$currentPath;C:\Program Files\Apache\maven\bin", "Machine")
    Log-Info "Maven installed"
}

# Install MySQL
Log-Info "Checking MySQL installation..."
if (Get-Service | Where-Object { $_.Name -like "*mysql*" }) {
    Log-Info "MySQL already installed"
} else {
    Log-Warn "MySQL not found. Installing MySQL..."
    Log-Info "Download MySQL Installer from: https://dev.mysql.com/downloads/installer/"
    Log-Info "During installation, select:"
    Log-Info "  - MySQL Server"
    Log-Info "  - MySQL Workbench (optional)"
    Log-Info "  - Set root password: (kosongkan atau set password)"
    Log-Info "After installation, run: mysql -u root -e 'CREATE DATABASE IF NOT EXISTS uasdisprog;'"
    Pause
}

# Install Node.js 18+
Log-Info "Checking Node.js installation..."
try {
    $nodeVersion = node --version
    $majorVersion = $nodeVersion -replace 'v', '' -split '\.' | Select-Object -First 1
    if ([int]$majorVersion -ge 18) {
        Log-Info "Node.js already installed (v$nodeVersion)"
    } else {
        Log-Warn "Node.js version found but less than 18. Installing Node.js 18+..."
        $nodeUrl = "https://nodejs.org/dist/v18.20.3/node-v18.20.3-x64.msi"
        $nodePath = "$env:TEMP\node-installer.msi"
        Invoke-WebRequest -Uri $nodeUrl -OutFile $nodePath
        Start-Process msiexec.exe -ArgumentList "/i `"$nodePath`" /qn" -Wait
        Log-Info "Node.js 18+ installed"
    }
} catch {
    Log-Info "Node.js not found. Installing Node.js 18+..."
    $nodeUrl = "https://nodejs.org/dist/v18.20.3/node-v18.20.3-x64.msi"
    $nodePath = "$env:TEMP\node-installer.msi"
    Invoke-WebRequest -Uri $nodeUrl -OutFile $nodePath
    Start-Process msiexec.exe -ArgumentList "/i `"$nodePath`" /qn" -Wait
    Log-Info "Node.js 18+ installed"
}

# Setup project
Log-Info "Setting up project..."

# Setup TCP Server
Log-Info "Configuring TCP Server..."
$tcpConfigPath = "C:\Abel\open code\asbun\Disprog_UAS_Modern\tcp-server\src\main\resources\application.properties"
if (Test-Path $tcpConfigPath) {
    $content = Get-Content $tcpConfigPath
    $content = $content -replace "^tcp\.server\.port=.*", "tcp.server.port=6002"
    Set-Content $tcpConfigPath $content
    Log-Info "TCP Server configuration updated"
}

# Setup Backend
Log-Info "Building Backend..."
cd "C:\Abel\open code\asbun\Disprog_UAS_Modern\backend"
if (-not (Test-Path "target")) {
    mvn clean package -DskipTests
    Log-Info "Backend built successfully"
} else {
    Log-Info "Backend already built"
}

# Setup Frontend
Log-Info "Building Frontend..."
cd "C:\Abel\open code\asbun\Disprog_UAS_Modern\frontend"
if (-not (Test-Path "node_modules")) {
    npm install
    Log-Info "Frontend dependencies installed"
} else {
    Log-Info "Frontend dependencies already installed"
}

# Create startup script
Log-Info "Creating startup scripts..."

# Start Backend
cat > "C:\Abel\open code\asbun\Disprog_UAS_Modern\start-backend.ps1" << 'EOF'
Write-Host "Starting Backend on port 8080..." -ForegroundColor Green
cd "C:\Abel\open code\asbun\Disprog_UAS_Modern\backend"
java -jar target\*.jar
EOF

# Start TCP Server
cat > "C:\Abel\open code\asbun\Disprog_UAS_Modern\start-tcp.ps1" << 'EOF'
Write-Host "Starting TCP Server on port 6002..." -ForegroundColor Green
cd "C:\Abel\open code\asbun\Disprog_UAS_Modern\tcp-server"
java -jar target\*.jar
EOF

# Start Frontend
cat > "C:\Abel\open code\asbun\Disprog_UAS_Modern\start-frontend.ps1" << 'EOF'
Write-Host "Starting Frontend on port 3000..." -ForegroundColor Green
cd "C:\Abel\open code\asbun\Disprog_UAS_Modern\frontend"
npm run dev
EOF

# Create combined start script
cat > "C:\Abel\open code\asbun\Disprog_UAS_Modern\start-all.ps1" << 'EOF'
Write-Host "Starting UAS Disprog Services..." -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan

# Start TCP Server in separate process
Write-Host "Starting TCP Server on port 6002..." -ForegroundColor Yellow
Start-Process powershell -ArgumentList "-Command ""cd 'C:\Abel\open code\asbun\Disprog_UAS_Modern\tcp-server'; java -jar target\*.jar"""

Start-Sleep -Seconds 5

# Start Backend in separate process
Write-Host "Starting Backend on port 8080..." -ForegroundColor Yellow
Start-Process powershell -ArgumentList "-Command ""cd 'C:\Abel\open code\asbun\Disprog_UAS_Modern\backend'; java -jar target\*.jar"""

Start-Sleep -Seconds 10

# Start Frontend
Write-Host "Starting Frontend on port 3000..." -ForegroundColor Yellow
cd "C:\Abel\open code\asbun\Disprog_UAS_Modern\frontend"
npm run dev
EOF

# Setup MySQL database
Log-Info "Setting up MySQL database..."
try {
    $dbExists = mysql -u root -e "SELECT 1 FROM uasdisprog" 2>$null
    if ($LASTEXITCODE -eq 0) {
        Log-Info "MySQL database 'uasdisprog' already exists"
    } else {
        mysql -u root -e "CREATE DATABASE uasdisprog;"
        Log-Info "MySQL database 'uasdisprog' created successfully"
    }
} catch {
    Log-Warn "MySQL database setup failed. Please run manually: mysql -u root -e 'CREATE DATABASE uasdisprog;'"
}

# Summary
Write-Host ""
Write-Host "======================================" -ForegroundColor Cyan
Write-Host "Setup Complete!" -ForegroundColor Cyan
Write-Host "======================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Services:"
Write-Host "  [OK] Java 21 (JDK)"
Write-Host "  [OK] Maven"
Write-Host "  [OK] Node.js 18+"
Write-Host "  [OK] npm"
Write-Host ""
Write-Host "Project Setup:"
Write-Host "  [OK] Backend built"
Write-Host "  [OK] Frontend dependencies installed"
Write-Host ""
Write-Host "Quick Start Commands:"
Write-Host "  cd 'C:\Abel\open code\asbun\Disprog_UAS_Modern'"
Write-Host "  .\start-all.ps1    # Start all services"
Write-Host "  .\start-backend.ps1 # Start only backend"
Write-Host "  .\start-tcp.ps1     # Start only TCP server"
Write-Host "  .\start-frontend.ps1 # Start only frontend"
Write-Host ""
Write-Host "Access URLs:"
Write-Host "  - Frontend: http://localhost:3000"
Write-Host "  - Backend:  http://localhost:8080"
Write-Host "  - TCP:      localhost:6002"
Write-Host "  - MySQL:    localhost:3306"
Write-Host ""
Write-Host "Login Credentials:"
Write-Host "  - Customer: Fi / 1234, Ab / 5678, Dan / abcd"
Write-Host "  - Admin:    b / c"
Write-Host ""
