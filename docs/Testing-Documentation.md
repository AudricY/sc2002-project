# Testing Documentation

## Test Environment

- Java Version: JDK 8 or higher
- Operating System: Linux/Unix/Windows
- Compilation: javac with default settings
- Execution: Standard Java runtime

## Test Data Files

### students.csv
10 sample students across different years (1-4) and majors (Computer Science, Engineering, Business Administration, Information Systems)

### staff.csv
3 career center staff members from different departments

## Test Cases

### TC-001: System Initialization
**Objective**: Verify system starts and loads initial data

**Steps**:
1. Execute `./run.sh`
2. Observe startup messages

**Expected**:
- "Internship Placement Management System initialized"
- "Data loaded from CSV files"
- Login menu displays

**Result**: ✓ PASS

---

### TC-002: Student Login with Default Password
**Objective**: Verify student can login with default credentials

**Steps**:
1. Select option 1 (Login)
2. Enter User ID: S001
3. Enter Password: password

**Expected**:
- Login successful
- First login password change prompt appears
- After password change, student menu displays

**Result**: ✓ PASS

---

### TC-003: Staff Login and Representative Approval
**Objective**: Verify staff can approve company representative registrations

**Steps**:
1. Login as STAFF001 (password: password)
2. Select "Review Company Representative Registrations"
3. Select a pending representative
4. Choose "Approve"

**Expected**:
- Representative status changes to APPROVED
- Representative can now login

**Result**: ✓ PASS

---

### TC-004: Company Representative Registration
**Objective**: Verify new company representative can register

**Steps**:
1. Select option 2 (Register)
2. Enter valid details:
   - Name: John Smith
   - Email: john.smith@techcorp.com
   - Password: password123
   - Company: TechCorp
   - Department: HR
   - Position: Recruiter

**Expected**:
- Registration successful message
- Account pending approval notification
- Login fails until approved by staff

**Result**: ✓ PASS

---

### TC-005: Company Representative Creates Internship
**Objective**: Verify approved representative can create internship opportunity

**Prerequisites**: Representative account approved by staff

**Steps**:
1. Login as approved company representative
2. Select "Create Internship Opportunity"
3. Enter details:
   - Title: Software Developer Intern
   - Description: Full-stack development position
   - Level: 2 (INTERMEDIATE)
   - Major: Computer Science
   - Opening Date: 2025-01-01
   - Closing Date: 2025-12-31
   - Slots: 5

**Expected**:
- Internship created with PENDING status
- Internship ID generated and displayed
- Internship not visible to students until approved

**Result**: ✓ PASS

---

### TC-006: Staff Approves Internship
**Objective**: Verify staff can approve pending internships

**Steps**:
1. Login as career center staff
2. Select "Review Internship Opportunities"
3. Select pending internship
4. Choose "Approve"

**Expected**:
- Internship status changes to APPROVED
- Internship becomes visible to eligible students
- Confirmation message displayed

**Result**: ✓ PASS

---

### TC-007: Student Views Available Internships
**Objective**: Verify students see only eligible, visible internships

**Steps**:
1. Login as S001 (Year 2, Computer Science)
2. Select "View Available Internships"

**Expected**:
- Only BASIC level internships displayed (Year 2 restriction)
- Only approved and visible internships shown
- Internships sorted alphabetically by default

**Result**: ✓ PASS

---

### TC-008: Student Applies for Internship
**Objective**: Verify student can submit application

**Steps**:
1. Login as S001
2. Select "Apply for Internship"
3. Choose an available internship

**Expected**:
- Application submitted successfully
- Application status set to PENDING
- Application appears in "View My Applications"

**Result**: ✓ PASS

---

### TC-009: Three Application Limit Enforcement
**Objective**: Verify students cannot exceed 3 concurrent applications

**Steps**:
1. Login as student with 3 pending applications
2. Select "Apply for Internship"

**Expected**:
- Error message: "You already have 3 pending applications"
- Cannot apply for fourth internship

**Result**: ✓ PASS

---

### TC-010: Company Representative Reviews Applications
**Objective**: Verify representative can view and approve applications

**Steps**:
1. Login as company representative
2. Select "View Applications for Internship"
3. Select internship
4. Select "Review Application"
5. Choose an application
6. Select "Approve"

**Expected**:
- Application status changes to SUCCESSFUL
- Student can see updated status
- Confirmation message displayed

**Result**: ✓ PASS

---

### TC-011: Student Accepts Placement
**Objective**: Verify student can accept successful application

**Steps**:
1. Login as student with SUCCESSFUL application
2. Select "Accept Placement"
3. Choose the successful application

**Expected**:
- Application marked as confirmed
- Student's confirmedPlacementId set
- Internship confirmed slots incremented
- Other successful applications marked UNSUCCESSFUL
- Confirmation message displayed

**Result**: ✓ PASS

---

### TC-012: Internship Auto-Fill Status
**Objective**: Verify internship status changes to FILLED when all slots confirmed

**Steps**:
1. Create internship with 2 slots
2. Have 2 students apply
3. Representative approves both
4. Second student accepts placement

**Expected**:
- After second acceptance, internship status = FILLED
- Internship no longer shown to new applicants

**Result**: ✓ PASS

---

### TC-013: Student Requests Withdrawal
**Objective**: Verify student can request application withdrawal

**Steps**:
1. Login as student with active application
2. Select "Request Withdrawal"
3. Choose application
4. Enter reason: "Found alternative opportunity"

**Expected**:
- Withdrawal request created with PENDING status
- Request appears in staff review queue
- Application remains active until approved

**Result**: ✓ PASS

---

### TC-014: Staff Approves Withdrawal
**Objective**: Verify staff can approve withdrawal requests

**Steps**:
1. Login as career center staff
2. Select "Review Withdrawal Requests"
3. Choose pending request
4. Select "Approve"

**Expected**:
- Withdrawal status = APPROVED
- Application removed from system
- If confirmed placement: slot count decremented
- If confirmed placement: student's confirmedPlacementId cleared

**Result**: ✓ PASS

---

### TC-015: Representative Toggles Visibility
**Objective**: Verify visibility toggle affects student view only

**Steps**:
1. Login as company representative
2. Select "Toggle Internship Visibility"
3. Choose visible internship
4. Toggle to hidden
5. Login as student
6. View available internships

**Expected**:
- Internship hidden from student view
- Representative still sees internship in their list
- Applications to hidden internships remain active

**Result**: ✓ PASS

---

### TC-016: Filter Settings Persistence
**Objective**: Verify user filter settings persist across sessions

**Steps**:
1. Login as student
2. Configure filters (e.g., Level = BASIC, Sort = CLOSING_DATE)
3. Logout
4. Login again as same student
5. View available internships

**Expected**:
- Previously configured filters still active
- Internships filtered and sorted as configured
- Default settings for new users remain alphabetical

**Result**: ✓ PASS

---

### TC-017: Representative Five Internship Limit
**Objective**: Verify representatives cannot create more than 5 internships

**Steps**:
1. Login as representative with 5 existing internships
2. Select "Create Internship Opportunity"

**Expected**:
- Error message: "You have reached the maximum limit of 5 internships"
- Cannot create sixth internship

**Result**: ✓ PASS

---

### TC-018: Edit Restriction on Approved Internships
**Objective**: Verify approved internships cannot be edited

**Steps**:
1. Login as company representative
2. Select "Edit Internship Opportunity"
3. Choose APPROVED internship

**Expected**:
- Error message: "Cannot edit approved or filled internships"
- Internship remains unchanged

**Result**: ✓ PASS

---

### TC-019: Duplicate Application Prevention
**Objective**: Verify students cannot apply twice to same internship

**Steps**:
1. Login as student
2. Apply to internship X
3. Attempt to apply to internship X again

**Expected**:
- Error message: "You have already applied to this internship"
- No duplicate application created

**Result**: ✓ PASS

---

### TC-020: Year-Level Eligibility Enforcement
**Objective**: Verify Year 1-2 students restricted to BASIC internships

**Steps**:
1. Create INTERMEDIATE level internship
2. Login as Year 1 student
3. View available internships

**Expected**:
- INTERMEDIATE internship not displayed
- Only BASIC internships visible

**Result**: ✓ PASS

---

### TC-021: Password Change Functionality
**Objective**: Verify users can change passwords

**Steps**:
1. Login as any user
2. Select "Change Password"
3. Enter current password
4. Enter new password
5. Logout and login with new password

**Expected**:
- Password changed successfully
- Old password no longer works
- New password grants access

**Result**: ✓ PASS

---

### TC-022: Report Generation
**Objective**: Verify staff can generate various reports

**Steps**:
1. Login as career center staff
2. Select "Generate Reports"
3. Select "All Internships Report"

**Expected**:
- Report displays internships grouped by status
- Counts shown for each status
- All internship details visible

**Result**: ✓ PASS

---

### TC-023: Filtered Report by Level
**Objective**: Verify filtered reports work correctly

**Steps**:
1. Login as staff
2. Select "Generate Reports" > "Filtered Internships Report"
3. Filter by Level: BASIC

**Expected**:
- Only BASIC level internships displayed
- Other levels excluded
- Accurate count shown

**Result**: ✓ PASS

---

### TC-024: Student Applications Summary
**Objective**: Verify student summary report accuracy

**Steps**:
1. Login as staff
2. Select "Generate Reports" > "Student Applications Summary"

**Expected**:
- All students listed
- Application counts accurate
- Status breakdown (pending/successful/unsuccessful) correct
- Confirmed placement status shown

**Result**: ✓ PASS

---

### TC-025: Data Persistence Across Restarts
**Objective**: Verify all data persists after application restart

**Steps**:
1. Create internship, applications, withdrawal requests
2. Exit application
3. Restart application
4. Login and verify data

**Expected**:
- All internships present
- All applications present
- All users present
- All withdrawal requests present
- All filter settings present

**Result**: ✓ PASS

---

## Test Results Summary

| Category | Tests | Passed | Failed |
|----------|-------|--------|--------|
| Authentication | 3 | 3 | 0 |
| User Management | 3 | 3 | 0 |
| Internship Management | 5 | 5 | 0 |
| Application Workflow | 7 | 7 | 0 |
| Withdrawal Process | 2 | 2 | 0 |
| Business Rules | 5 | 5 | 0 |
| **TOTAL** | **25** | **25** | **0** |

## Coverage Analysis

### Entity Classes
- All entity classes tested through workflow scenarios
- Getter/setter operations verified implicitly
- Business logic methods (e.g., incrementConfirmedSlots) tested explicitly

### Control Classes
- All manager classes exercised through test cases
- Singleton instances verified functional
- CRUD operations for all entities tested
- Filter and search functionality validated

### Boundary Classes
- All menu options tested at least once
- Input validation verified through invalid input attempts
- Error messages confirmed user-friendly

### Edge Cases Tested
- Maximum limits (3 applications, 5 internships)
- Status transitions (PENDING→APPROVED→FILLED)
- Cascading updates (acceptance withdrawing other applications)
- Empty state handling (no applications, no internships)

## Known Limitations

1. **Concurrent Access**: Not tested as system assumes single-user access
2. **Large Data Sets**: Performance not tested with thousands of records
3. **Data Corruption**: No tests for manually corrupted data files
4. **Network Issues**: File I/O failures not simulated

## Test Execution Instructions

1. Ensure clean state: Delete `data/*.dat` files before each test suite run
2. CSV files must be present in `data/` directory
3. Run tests in sequence as some depend on state from previous tests
4. For independent test runs, reset system state between tests

## Automated Testing Recommendation

Future enhancement: JUnit test suite to automate these test cases with mock data and assertion validation.
