#!/bin/bash

echo "Compiling Internship Placement Management System..."

mkdir -p bin

javac -d bin -sourcepath src/main/java src/main/java/com/internship/InternshipPlacementSystem.java

if [ $? -eq 0 ]; then
    echo "Compilation successful!"
    echo "To run the application, execute: ./run.sh"
else
    echo "Compilation failed. Please check errors above."
    exit 1
fi
