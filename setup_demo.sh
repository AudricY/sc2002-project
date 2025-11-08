#!/bin/bash

# Setup script for demo - clears existing data and seeds demo data

echo "=========================================="
echo "DEMO SETUP: Clearing Data & Seeding Demo Data"
echo "=========================================="
echo ""

# Clear all data files
echo "Clearing existing data files..."
rm -f data/*.dat
rm -f data/id_counters.properties

echo "✅ Data files cleared"
echo ""

# Compile DemoDataSeeder if needed
echo "Compiling DemoDataSeeder..."
javac -d bin -sourcepath src/main/java src/main/java/util/DemoDataSeeder.java 2>/dev/null

# Seed demo data
echo "Seeding demo data..."
java -cp bin util.DemoDataSeeder

echo ""
echo "✅ Demo setup complete!"
echo ""
echo "Demo Company Rep: demo.rep@techcorp.com (password: password)"
echo "Created 8 internships (3 BASIC, 3 INTERMEDIATE, 2 ADVANCED)"
echo ""

