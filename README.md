# Internship Placement Management System

A comprehensive CLI-based system for managing internship placements between students, companies, and career center staff.

## Features

### Student Capabilities
- View available internships filtered by eligibility (year and major)
- Apply for up to 3 concurrent internships
- View application status (Pending, Successful, Unsuccessful)
- Accept placement offers
- Request withdrawal of applications
- Configure personal filter settings (level, major, sort order)

### Company Representative Capabilities
- Register and await approval from Career Center Staff
- Create up to 5 internship opportunities
- Edit unapproved internship postings
- Toggle internship visibility
- View applications with student details
- Approve or reject student applications

### Career Center Staff Capabilities
- Approve or reject company representative registrations
- Review and approve internship opportunities
- Process student withdrawal requests
- Generate comprehensive reports
- View all students and their application status

## Compilation and Execution

### Compile
```bash
javac -d bin -sourcepath src src/InternshipPlacementSystem.java
```

### Run
```bash
java -cp bin InternshipPlacementSystem
```

## Initial Setup

1. The system loads students from `data/students.csv`
2. The system loads staff from `data/staff.csv`
3. Default password for preloaded users: `password`
4. Company representatives must register through the system

## Sample Login Credentials

### Students
- User ID: S001, S002, ..., S010
- Password: password (default)

### Career Center Staff
- User ID: STAFF001, STAFF002, STAFF003
- Password: password (default)

### Company Representatives
- Must register first through option 2 on login menu

## Data Storage

All data is stored in serialized files in the `data/` directory:
- users.dat
- internships.dat
- applications.dat
- withdrawals.dat
- filters.dat

## Project Structure

```
src/
├── entity/           # Entity classes (User hierarchy, Internship, Application, etc.)
├── control/          # Control classes (Managers and Controllers)
├── boundary/         # Boundary classes (Menus and UI)
├── util/             # Utility classes (FileManager, Validators, IdGenerator)
└── InternshipPlacementSystem.java  # Main entry point

data/                 # Data files
docs/                 # Documentation and diagrams
```
