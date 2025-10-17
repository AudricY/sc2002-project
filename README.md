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

### Using Maven (Recommended)

#### Compile
```bash
mvn compile
```

#### Run
```bash
mvn exec:java -Dexec.mainClass="InternshipPlacementSystem"
```

#### Run Tests
```bash
mvn test
```

#### Run Specific Test Class
```bash
mvn test -Dtest=AuthenticationTests
mvn test -Dtest=ApplicationWorkflowTests
```

#### Clean and Rebuild
```bash
mvn clean compile
```

### Using javac (Manual)

#### Compile
```bash
javac -d bin -sourcepath src src/InternshipPlacementSystem.java
```

#### Run
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

src/test/java/        # Automated test suite
├── util/             # Test utilities
│   ├── TestStateManager.java
│   ├── TestDataSetup.java
│   └── TestHelpers.java
├── AuthenticationTests.java       # TC-001, TC-002, TC-003
├── UserManagementTests.java       # TC-004, TC-021
├── InternshipManagementTests.java # TC-005, TC-006, TC-015, TC-017, TC-018
├── ApplicationWorkflowTests.java  # TC-007 through TC-012, TC-019
├── WithdrawalProcessTests.java    # TC-013, TC-014
└── BusinessRulesTests.java        # TC-016, TC-020, TC-022, TC-023, TC-024, TC-025
```

## Automated Testing

### Test Suite Overview

The project includes a comprehensive automated test suite with 25+ test cases covering all major functionality:

- **Authentication Tests** (3 tests): System initialization, login, password changes
- **User Management Tests** (2 tests): Registration and account management
- **Internship Management Tests** (5 tests): Creation, approval, visibility, limits
- **Application Workflow Tests** (7 tests): Viewing, applying, approving, accepting placements
- **Withdrawal Process Tests** (3 tests): Requesting and approving withdrawals
- **Business Rules Tests** (6 tests): Filter persistence, eligibility, reports, data persistence

### Running the Tests

#### Run All Tests
```bash
mvn test
```

#### Run Specific Test Class
```bash
mvn test -Dtest=AuthenticationTests
mvn test -Dtest=ApplicationWorkflowTests
```

#### Run with Detailed Output
```bash
mvn test -Dtest=AuthenticationTests -DargLine="-Djunit.platform.output.capture.stdout=true"
```

### Test Results

After running tests, Maven will display a summary:
```
[INFO] Tests run: 25, Failures: 0, Errors: 0, Skipped: 0
```

Detailed test reports are generated in:
- Console output: Immediate feedback
- Target directory: `target/surefire-reports/`

### Test Coverage

The automated tests cover:
- All user roles (Student, Company Representative, Career Center Staff)
- Complete application lifecycle (apply → approve → accept → withdraw)
- Business rules enforcement (application limits, eligibility, slot management)
- Data persistence across system restarts
- Filter settings and report generation
- Edge cases and error conditions

### Test Data

Tests automatically:
- Create clean test data before each test
- Reset system state between tests
- Use the same CSV format as production
- Clean up after test execution

No manual setup required - just run `mvn test`!
