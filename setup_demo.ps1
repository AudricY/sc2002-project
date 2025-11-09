# DemoLaunch.ps1
# Combined Demo Setup and Startup for Windows

Write-Host "=========================================="
Write-Host "DEMO SETUP & LAUNCH: Internship Placement Management System"
Write-Host "=========================================="
Write-Host ""

# Ensure required directories exist
if (-Not (Test-Path -Path "data")) {
    Write-Host "Creating data directory..."
    New-Item -ItemType Directory -Path "data" | Out-Null
}

if (-Not (Test-Path -Path "bin")) {
    Write-Host "Creating bin directory..."
    New-Item -ItemType Directory -Path "bin" | Out-Null
}

# Clear all data files
Write-Host "Clearing existing data files..."
Get-ChildItem -Path "data" -Filter "*.dat" -ErrorAction SilentlyContinue | Remove-Item -Force -ErrorAction SilentlyContinue
if (Test-Path "data\id_counters.properties") {
    Remove-Item "data\id_counters.properties" -Force
}
Write-Host "✅ Data files cleared"
Write-Host ""

# Compile DemoDataSeeder
Write-Host "Compiling DemoDataSeeder..."
javac -d bin -sourcepath src\main\java src\main\java\util\DemoDataSeeder.java

if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ Compilation failed! Aborting demo."
    exit 1
} else {
    Write-Host "✅ Compilation successful"
}

# Seed demo data
Write-Host "Seeding demo data..."
java -cp bin util.DemoDataSeeder

if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ Demo data seeding failed! Aborting demo."
    exit 1
} else {
    Write-Host "✅ Demo data seeded successfully"
}

# Compile main application
Write-Host ""
Write-Host "Compiling main application..."
javac -d bin -sourcepath src\main\java src\main\java\com\internship\InternshipPlacementSystem.java

if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ Main application compilation failed! Aborting demo."
    exit 1
} else {
    Write-Host "✅ Main application compiled successfully"
}

# Launch the application
Write-Host ""
Write-Host "=========================================="
Write-Host "Starting Internship Placement Management System..."
Write-Host "=========================================="
Write-Host ""

java -cp bin com.internship.InternshipPlacementSystem
