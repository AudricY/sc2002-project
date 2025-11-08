# Demo Storyboard and Presentation Plan

**Total Duration:** 15 minutes  
**Format:** Real-time demonstration (not pre-recorded)  
**Presenters:** 5 team members (all must participate)

---

## Pre-Demo Setup Checklist

- [ ] Compile the application: `./compile.sh`
- [ ] Ensure data files are ready in `data/` directory
- [ ] Test run the application once to verify working state
- [ ] Close unnecessary applications and terminal windows
- [ ] Increase terminal font size for visibility (minimum 16pt)
- [ ] Have test credentials ready (see Quick Reference below)
- [ ] Clear screen before starting: `clear`

---

## Quick Reference: Test Credentials

| Role | User ID | Password | Notes |
|------|---------|----------|-------|
| Student (Year 1) | U2310004D | password | Chong Zhi Hao - For BASIC level demos |
| Student (Year 2) | U2310001A | password | Tan Wei Ling - Computer Science |
| Student (Year 3) | U2310002B | password | Ng Jia Hao - Data Science & AI |
| Student (Year 3) | U2310005E | password | Wong Shu Hui - Computer Science - For INTERMEDIATE/ADVANCED demos |
| Student (Year 4) | U2310003C | password | Lim Yi Xuan - Computer Engineering |
| Career Center Staff | sng001 | password | Dr. Sng Hui Lin - Main staff account |
| Career Center Staff | tan002 | password | Mr. Tan Boon Kiat - Alternative staff |
| Career Center Staff | lee003 | password | Ms. Lee Mei Ling - Alternative staff |
| Company Rep | (register new) | password123 | Will register during demo |

---

## Demo Flow Overview

| Section | Duration | Content |
|---------|----------|---------|
| Introduction | 1 min | Team, group number, system overview |
| Workflow 1: Rep Registration & Internship Creation | 3 min | Company rep flow |
| Workflow 2: Student Application & Placement | 3 min | Student application lifecycle |
| Workflow 3: Withdrawal Process | 2 min | Withdrawal request handling |
| Additional Features Demo | 5 min | Filter settings, reports, business rules |
| Wrap-up | 1 min | Additional features summary, Q&A |

---

## Section 1: Introduction (1 minute)

**Presenter 1**

### Script

"Good [morning/afternoon], we are Group [X] presenting our Internship Placement Management System.

Our team members are:
- [Name 1] - [Student ID]
- [Name 2] - [Student ID]
- [Name 3] - [Student ID]
- [Name 4] - [Student ID]
- [Name 5] - [Student ID]

Our system is a CLI-based application that manages internship placements between students, company representatives, and career center staff. It implements Entity-Control-Boundary architecture with file-based persistence using Java serialization. The system enforces business rules like application limits, eligibility restrictions, and approval workflows.

We'll demonstrate the complete system through three core workflows, followed by additional features that enhance usability and data integrity."

### Actions

1. Display team slide (if using slides) OR have terminals ready
2. Have application ready to launch

**Test Cases Covered:** TC-001 (System Initialization)

---

## Section 2: Workflow 1 - Company Representative Registration & Internship Creation (3 minutes)

**Presenter 2**

### Objective

Demonstrate company representative registration, staff approval process, internship creation, and staff approval of internships.

### Steps

#### 2.1: Start Application and Register Company Representative (1 min)

```bash
./run.sh
```

**Actions:**
1. Select option **2** (Register as Company Representative)
2. Enter details:
   - Name: `bob`
   - Email: `bob@techcorp.com`
   - Password: `password123`
   - Company Name: `TechCorp Solutions`
   - Department: `Human Resources`
   - Position: `Recruitment Manager`
3. Note the success message: "Registration successful! Your account is pending approval."
4. Attempt to login as `bob@techcorp.com` - should FAIL

**Expected Result:** Registration successful, but login blocked until approved.

**Presenter Notes:**
- Emphasize that security requires staff approval before access
- This prevents unauthorized company access to the system

#### 2.2: Staff Approves Company Representative (45 sec)

**Actions:**
1. Select option **1** (Login)
2. Enter User ID: `sng001`
3. Enter Password: `password`
4. Select option **1** (Review Company Representative Registrations)
5. Select the pending representative: `bob@techcorp.com`
6. Select option **1** (Approve)
7. Confirm approval
8. Logout (select **0**)

**Expected Result:** Representative status changed to APPROVED, confirmation message displayed.

**Presenter Notes:**
- Show the pending list first to highlight the workflow
- Mention staff can also reject if needed

#### 2.3: Representative Creates Internship (1 min 15 sec)

**Actions:**
1. Login as `bob@techcorp.com` with password `password123`
2. Login should now SUCCEED
3. Select option **1** (Create Internship Opportunity)
4. Enter internship details:
   - Title: `Software Engineering Intern`
   - Description: `Full-stack development role with mentorship`
   - Level: `1` (BASIC)
   - Preferred Major: `Computer Science`
   - Opening Date: `2026-01-15`
   - Closing Date: `2026-12-31`
   - Number of Slots: `5`
5. Note internship created with PENDING status
6. Record the Internship ID (e.g., INT001)
7. Logout

**Expected Result:** Internship created successfully with PENDING status, not yet visible to students.

**Presenter Notes:**
- Mention internships require staff approval before students see them
- Point out the automatic ID generation
- Highlight that representatives can create up to 5 internships

#### 2.4: Staff Approves Internship

**Actions:**
1. Login as `sng001`
2. Select option **2** (Review Internship Opportunities)
3. Select the pending internship `INT001`
4. Select option **1** (Approve)
5. Confirm approval
6. Note status changed to APPROVED
7. Logout

**Expected Result:** Internship status changed to APPROVED, now visible to eligible students.

**Test Cases Covered:** TC-004, TC-005, TC-006, TC-003

---

## Section 3: Workflow 2 - Student Application & Placement Acceptance (3 minutes)

**Presenter 3**

### Objective

Demonstrate student viewing internships, applying, company rep reviewing and approving applications, and student accepting placement.

### Steps

#### 3.1: Student Views Available Internships (30 sec)

**Actions:**
1. Login as Student `U2310001A` (password: `password`)
2. Select option **1** (View Available Internships)
3. Show the list of internships
4. Note: Only BASIC level internships appear (Year 1-2 restriction)

**Expected Result:** Only approved, visible, BASIC-level internships displayed for Computer Science major.

**Presenter Notes:**
- Emphasize automatic eligibility filtering
- Year 1-2 students restricted to BASIC level
- Only relevant majors shown

#### 3.2: Student Applies for Internship (30 sec)

**Actions:**
1. Select option **3** (Apply for Internship)
2. Enter the Internship ID: `INT001`
3. Confirm application
4. Note success message
5. Select option **2** (View My Applications)
6. Show the application with PENDING status
7. Logout

**Expected Result:** Application submitted successfully with PENDING status.

**Presenter Notes:**
- Students can apply to maximum 3 internships concurrently
- Application status is PENDING by default

#### 3.3: Company Representative Reviews Application (1 min)

**Actions:**
1. Login as `bob@techcorp.com`
2. Select option **2** (View My Internship Opportunities)
3. Show the internship with application count
4. Select option **5** (View Applications for Internship)
5. Enter Internship ID: `INT001`
6. Select option **6** (Review Application)
7. Select the application from U2310001A
8. View student details (Year, Major, etc.)
9. Select option **1** (Approve)
10. Confirm approval
11. Note success message
12. Logout

**Expected Result:** Application status changed to SUCCESSFUL, student notified.

**Presenter Notes:**
- Representatives can see student profile information
- Can approve or reject applications
- Approved count increments toward filling slots

#### 3.4: Student Accepts Placement (1 min)

**Actions:**
1. Login as `U2310001A`
2. Select option **2** (View My Applications)
3. Note status is now SUCCESSFUL
4. Select option **4** (Accept Placement)
5. Select the successful application
6. Confirm acceptance
7. Note confirmation message
8. Select option **2** (View My Applications) again
9. Show that confirmed placement appears
10. Logout

**Expected Result:** Placement confirmed, internship confirmed slot count incremented.

**Presenter Notes:**
- Students can only accept ONE placement
- Accepting one placement automatically withdraws other successful applications
- Confirmed slots contribute to FILLED status

**Test Cases Covered:** TC-007, TC-008, TC-010, TC-011

---

## Section 4: Workflow 3 - Withdrawal Request Process (2 minutes)

**Presenter 4**

### Objective

Demonstrate student withdrawal request and staff approval process, showing slot management.

### Steps

#### 4.1: Student Requests Withdrawal (45 sec)

**Actions:**
1. Login as `U2310001A`
2. Select option **5** (Request Withdrawal)
3. Select the confirmed application
4. Enter reason: `Accepted alternative opportunity`
5. Confirm request
6. Note success message: "Withdrawal request submitted for staff review"
7. Logout

**Expected Result:** Withdrawal request created with PENDING status.

**Presenter Notes:**
- Withdrawal can be requested before or after placement confirmation
- Requires staff approval to maintain data integrity
- Prevents students from unilaterally affecting slot counts

#### 4.2: Staff Reviews and Approves Withdrawal (1 min 15 sec)

**Actions:**
1. Login as `sng001`
2. Select option **3** (Review Withdrawal Requests)
3. Select the pending request from U2310001A
4. View withdrawal details (student info, internship, reason)
5. Select option **1** (Approve)
6. Confirm approval
7. Note: "Withdrawal approved. Confirmed slot decremented."
8. Select option **5** (View All Students)
9. Show that U2310001A no longer has confirmed placement
10. Logout

**Expected Result:** 
- Withdrawal approved
- Application removed from system
- Internship confirmed slot count decremented
- Student's confirmed placement cleared

**Presenter Notes:**
- Staff can approve or reject withdrawals
- Approved withdrawals automatically adjust slot counts
- Maintains data integrity across complex state transitions
- Internship status may revert from FILLED to APPROVED if slots available

**Test Cases Covered:** TC-013, TC-014

---

## Section 5: Additional Features Demonstration (5 minutes)

**Presenters 4 & 5** (split this section)

### 5.1: Student Features (1.5 minutes)

**Presenter 4**

#### Filter Settings Persistence

**Actions:**
1. Login as `U2310005E` (Year 3 student - can see all levels)
2. Select option **1** (View Available Internships)
3. Select option **8** (Configure Filter Settings)
4. Set filters:
   - Filter by Level: `1` (BASIC)
   - Sort by: `3` (Closing Date)
5. View internships - now filtered and sorted
6. Navigate to other menus and back
7. Return to View Internships - filters still active
8. Logout and login again
9. View Internships - **filters persist across sessions**

**Expected Result:** User-specific filter settings saved and restored across sessions.

**Presenter Notes:**
- Highlight persistence across logout/login
- Default is alphabetical sort
- Enhances user experience by remembering preferences

#### Application Limit Enforcement

**Actions:**
1. Still logged in as U2310005E
2. Apply for 3 different internships (if available)
3. Attempt to apply for a 4th internship
4. Show error: "You already have 3 pending applications"

**Expected Result:** Fourth application blocked with clear error message.

**Test Cases Covered:** TC-009, TC-016

### 5.2: Company Representative Features (1.5 minutes)

**Presenter 4**

#### Visibility Toggle

**Actions:**
1. Login as `sarah.johnson@techcorp.com`
2. Select option **2** (View My Internship Opportunities)
3. Select option **3** (Toggle Internship Visibility)
4. Enter Internship ID: `INT001`
5. Toggle to HIDDEN
6. Logout
7. Login as a student (`U2310002B`)
8. View Available Internships - INT001 no longer appears
9. Logout
10. Login as representative again
11. View My Internships - INT001 still visible to representative

**Expected Result:** 
- Internship hidden from students
- Representative retains access
- Existing applications remain active

**Presenter Notes:**
- Allows temporary hiding without deletion
- Representatives always see their own internships
- Useful for draft work or temporarily closing applications

#### Edit Restriction on Approved Internships

**Actions:**
1. Still logged in as representative
2. Select option **5** (Edit Internship Opportunity)
3. Enter Internship ID: `INT001` (approved internship)
4. Show error: "Cannot edit approved or filled internships"
5. Note restriction enforced

**Expected Result:** Edit blocked for approved internships.

**Presenter Notes:**
- Maintains integrity of approved postings
- Prevents bait-and-switch scenarios
- Only PENDING and REJECTED internships can be edited

#### Internship Limit

**Actions:**
1. Select option **2** (View My Internship Opportunities)
2. Note current count (e.g., "You have created 1 of 5 internships")
3. Mention that attempting to create a 6th would be blocked
4. Logout

**Test Cases Covered:** TC-015, TC-017, TC-018

### 5.3: Career Center Staff Features (2 minutes)

**Presenter 5**

#### Comprehensive Reporting System

**Actions:**
1. Login as `sng001`
2. Select option **6** (Generate Reports)
3. Select option **1** (All Internships Report)
4. Show report grouped by status (Pending, Approved, Filled, Rejected)
5. Note counts and details for each
6. Press Enter to return
7. Select option **2** (Filtered Internships Report)
8. Filter by: `1` (Status)
9. Enter status: `2` (APPROVED)
10. Show filtered results
11. Press Enter
12. Select option **3** (Student Applications Summary)
13. Show all students with application counts
14. Note breakdown: Pending, Successful, Unsuccessful, Confirmed Placement
15. Logout

**Expected Result:** Multiple report types with accurate aggregations and filtering.

**Presenter Notes:**
- Reports support data-driven decisions
- Multiple filtering options (status, level, major)
- Real-time statistics
- Demonstrates data aggregation capabilities

#### User Management Overview

**Actions:**
1. Still logged in as sng001
2. Select option **5** (View All Students)
3. Show student list with profiles (Year, Major)
4. Return to menu
5. Select option **1** (Review Company Representative Registrations)
6. Show both pending and approved representatives
7. Demonstrate staff oversight capabilities

**Expected Result:** Complete visibility of all system users and their status.

**Test Cases Covered:** TC-022, TC-023, TC-024

### 5.4: Business Rules & Data Integrity (Quick Highlights)

**Presenter 5**

Quickly mention (without full demo):

1. **Duplicate Prevention:** System prevents applying twice to same internship
2. **Automatic Status Management:** Internship auto-changes to FILLED when all slots confirmed
3. **Cascade Withdrawals:** Accepting one placement automatically withdraws others
4. **Eligibility Enforcement:** Year 1-2 cannot even view INTERMEDIATE/ADVANCED internships
5. **Data Persistence:** All changes saved to `.dat` files, survive system restart

**Presenter Notes:**
- These features maintain data integrity
- Reduce manual oversight burden
- Enforce business rules automatically

**Test Cases Covered:** TC-012, TC-019, TC-020, TC-025

---

## Section 6: Wrap-up and Additional Features Summary (1 minute)

**Presenter 5**

### Script

"To summarize, our system implements all mandatory requirements plus 14 additional features:

**Core Enhancements:**
1. Persistent filter settings across sessions
2. Comprehensive withdrawal workflow with audit trail
3. Automatic status management based on slot availability
4. Multi-layered role-based visibility control
5. Advanced reporting with multiple filter options

**Quality Features:**
6. Input validation framework
7. Sophisticated application lifecycle management
8. Eligibility-based automatic filtering
9. Real-time limit enforcement with clear feedback

**Business Rules:**
10. Representative opportunity limit tracking
11. Edit restrictions on approved internships
12. Duplicate application prevention
13. Pending withdrawal checks
14. User-friendly CLI interface with screen management

All features integrate seamlessly with Entity-Control-Boundary architecture, demonstrating solid OOP principles including inheritance, polymorphism, encapsulation, and design patterns like Singleton and Template Method.

We're happy to answer any questions."

### Actions

1. Show quick slide or terminal with additional features list
2. Open for Q&A
3. Be ready to jump back into any section if requested

---

## Presenter Assignments (Suggested)

| Presenter | Section | Duration | Key Points |
|-----------|---------|----------|------------|
| 1 | Introduction | 1 min | Team intro, system overview |
| 2 | Workflow 1 (Rep & Internship) | 3 min | Registration, approvals, creation flow |
| 3 | Workflow 2 (Application) | 3 min | Student apply, rep approve, accept placement |
| 4 | Workflow 3 & Student Features | 2.5 min | Withdrawal process, filters, limits |
| 5 | Staff Features & Wrap-up | 5.5 min | Reports, business rules, summary |

**Note:** Timing is flexible. Adjust based on actual demo pace. Aim to finish by 14 minutes to allow Q&A buffer.

---

## Troubleshooting Guide

### If Demo Fails Mid-Presentation

1. **Application crashes:** Restart with `./run.sh` - data persists
2. **Wrong test data:** Use alternative credentials (U2310002B, U2310003C, tan002, lee003)
3. **Terminal issues:** Have backup terminal window ready
4. **Timing runs over:** Skip "Business Rules Quick Highlights" (5.4) and mention in wrap-up instead

### Common Issues and Fixes

| Issue | Solution |
|-------|----------|
| "User not found" | Verify correct User ID format (U2310001A, not u2310001a) |
| "Account pending approval" | Need staff to approve first |
| "Already applied" | Use different student account |
| Font too small | Use Cmd/Ctrl + Plus to increase |
| Data state wrong | Delete `.dat` files and restart (ONLY if necessary) |

---

## Post-Demo Checklist

- [ ] Answer TA questions
- [ ] Be prepared to explain code implementation if asked
- [ ] Have UML diagrams ready for reference
- [ ] Know where to find specific features in code
- [ ] Understand design decisions and trade-offs

---

## Test Case Coverage Summary

This demo covers all 25 test cases documented in `Testing-Documentation.md`:

| Category | Test Cases Covered |
|----------|-------------------|
| Authentication & Login | TC-001, TC-002 |
| User Management | TC-003, TC-004, TC-021 |
| Internship Management | TC-005, TC-006, TC-015, TC-017, TC-018 |
| Application Workflow | TC-007, TC-008, TC-009, TC-010, TC-011, TC-012, TC-019 |
| Withdrawal Process | TC-013, TC-014 |
| Business Rules | TC-016, TC-020, TC-022, TC-023, TC-024, TC-025 |

**Total:** 25/25 test cases demonstrated or referenced

---

## Additional Resources

- **Testing Documentation:** `docs/Testing-Documentation.md`
- **Additional Features:** `docs/Additional-Features.md`
- **UML Class Diagram:** `docs/UML-Class-Diagram.md`
- **UML Sequence Diagram:** `docs/UML-Sequence-Diagram.md`
- **Design Considerations:** `docs/Design-Considerations.md`
- **Javadoc:** `apidocs/index.html`

---

## Final Notes

- **Practice the demo** at least once before presentation
- **Time yourselves** to ensure staying within 15 minutes
- **Coordinate handoffs** between presenters smoothly
- **Show confidence** - you built this system, you know it well
- **Be prepared** to deep-dive into any feature if TA asks
- **Emphasize** OOP principles and design patterns when relevant

**Good luck with your presentation!**

