#!/bin/bash

if [ ! -d "bin" ]; then
    echo "Application not compiled. Please run: ./compile.sh"
    exit 1
fi

echo "Starting Internship Placement Management System..."
echo ""

java -cp bin InternshipPlacementSystem
