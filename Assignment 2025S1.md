# SC/CE/CZ2002: Object-Oriented Design & Programming
## ASSIGNMENT
### Building an OO Application

**2025/2026 SEMESTER 1**

**COLLEGE OF COMPUTING AND DATA SCIENCE (CCDS)**
**NANYANG TECHNOLOGICAL UNIVERSITY**

---

## 1. OBJECTIVES

The main objective of this assignment is:
*   to apply the Object-Oriented (OO) concepts you have learnt in the course,
*   to model, design and develop an OO application.
*   to gain familiarity with using Java as an object-oriented programming language.
*   to work collaboratively as a group to achieve a common goal.

## 2. LABORATORY

Assigned CCDS lab.

## 3. EQUIPMENT

*   **Hardware:** PC (or Laptop)
*   **Software:** Your preferred Java IDE (e.g., IntelliJ, Eclipse, NetBeans) or text editor with JDK installed. Visual paradigm.

## 4. THE ASSIGNMENT

The assignment for your group will be to design and develop a:

**Internship Placement Management System**

The following are information about the system:

### Overview of the System:
*   The system will act as a centralized hub for all Students, Company Representatives, and Career Center Staff.
*   All users will need to login to this hub using their account.
    *   Students' IDs will start with U, followed by 7-digit numbers and ends with a letter (e.g., U2345123F)
    *   Company representatives ID is their company email address.
    *   Career Center Staff's ID is their NTU account.
    *   Assume all users use the default password, which is password.
    *   A user can change password in the system.
*   Additional Information about the user:
    *   Student: Year of Study and Major
    *   Company Representatives: Company Name, Department, and position.
    *   Carrer Center Staff: Staff Department
*   A user list can be initiated through a file uploaded into the system at initialization.

### User's capabilities:

#### 1. All Users
*   All users should have a User ID, Name and Password
*   They should all have access to the basic user management features, including login, logout and change password

#### 2. Student
*   Possesses all the base user capabilities
*   Registration is automatic by reading in from the student list file.
*   Can only view the list of internship opportunities based:
    *   Student's own profile:
        *   Year of study (Year 1 to 4)
        *   Major (CSC, EEE, MAE, etc...)
    *   Visibility has been toggled "on"
*   Able to apply for a maximum of 3 internship opportunities at once
    *   Year 1 and 2 students can ONLY apply for Basic-level internships
    *   Year 3 and above students can apply for any level (Basic, Intermediate, Advanced)
*   Able to view the internship he/she applied for, even after visibility is turned off, and the application status (“Pending”, “Successful" or "Unsuccessful")
    *   Status will be “Pending” by default, and updated based on the input from the Company Representative
*   If application status is "Successful", students can accept the internship placement
    *   Only 1 internship placement can be accepted
    *   All other applications will be withdrawn once an internship placement is accepted
*   Allowed to request withdrawal for their internship application before/after placement confirmation
    *   Subject to approval from Career Center Staff

#### 3. Company Representatives
*   Company Representative list is empty at very beginning.
*   Company Representatives must register as a representative of a specific company, and they can only log in once approved by the Career Center Staff.
*   Able to create internship opportunities (up to 5) for their companies, which should include the following details:
    *   Internship Title
    *   Description
    *   Internship Level (Basic, Intermediate, Advanced)
    *   Preferred Majors (Assume 1 preferred major will do)
    *   Application opening date
    *   Application closing date
    *   Status ("Pending”, “Approved”, “Rejected", "Filled")
    *   Company Name
    *   Company Representatives in charge (automatically assigned)
    *   Number of slots (max of 10)
*   Internship opportunities created must be approved by the career center staff
    *   Once status is “Approved”, students may apply for them
    *   If "Filled" or after the Closing Date, students will not be able to apply for them anymore
*   Able to view application details and student details for each of their internship opportunities
*   May Approve or Reject the internship application
    *   Once approved, student application status becomes "Successful"
    *   Student can then accept the placement confirmation
    *   Internship opportunity status becomes "Filled" only when all available slots are confirmed by students
*   Able to toggle the visibility of the internship opportunity to “on” or “off”. This will be reflected in the internship list that will be visible to Students

#### 4. Career Center Staff
*   Registration is automatic by reading in from the staff list file.
*   Able to authorize or reject the account creation of Company Representatives
*   Able to approve or reject internship opportunities submitted by Company Representatives
    *   Once approved, internship opportunity status changes to "Approved" and becomes visible to eligible students
*   Able to approve or reject student withdrawal requests (both before and after placement confirmation)
*   Able to generate comprehensive reports regarding internship opportunities created:
    *   There should be filters to generate filter opportunities based on their Status, Preferred Majors, Internship Level, etc...

### Miscellaneous:
*   All users can use filters to view internship opportunities (Status, Preferred Majors, Internship Level, Closing Date, etc.) Assume that default is by alphabetical order. User filter settings are saved when they switch menu pages.

---

To reduce the workload, the system will be developed as a Command Line Interface (CLI) application. GUI implementation is optional and will not carry any bonus marks, as the focus is on Object-Oriented Design and Programming (OODP).

The sample data file for user list is given in excel in assignment folder. You can
*   Use them directly,
*   Or copy the content to text file if you plan to read from text file,
*   Or make your own data files.

But **No database application (e.g. MySQL, MS Access, etc) is to be used.**
**No JSON or XML is to be used.**

## 5. THE REPORT

Your report will include the following:
a)  A detailed UML **Class Diagram** for the application (exported as an image)
    *   Show clearly the class relationship, notation
    *   Notes to explain, if necessary
    *   Annotate your UML diagram to highlight where specific OO principles (e.g., encapsulation, polymorphism) are applied.
b)  A detailed UML **Sequence Diagram** (exported as an image)
    *   Showing a flow of the Company Representative's Role in Internship Management and Application Process. E.g., student application review processes.
    *   The diagram should show clearly all participating objects involved with sufficient detailed flow and relevant interaction fragments.
c)  Highlight clearly any **additional features/functionalities implemented in the system**.
d)  Based on the concepts learned in the lecture, **write-up on your design considerations** and use of OO concepts in your current design, extensibility and maintainability of your design. Discuss any trade-offs you made in your design and reflect on how the design patterns you used contribute to the overall system design. Were there alternative patterns you considered? Why did you choose the ones you did?
e)  **Reflection**: The difficulties encountered and the way to conquer, the knowledge learnt from this course, further improvement suggestions. Strong demonstration of learning points and insights of good design and implementation practices, based on experience gained from doing the assignment.
f)  Include the **link to your GitHub repository** used for this project, containing all the relevant files and code.
g)  A duly signed **Declaration of Original Work** form (Appendix B)
h)  By default, all group members will receive the same mark and do **NOT** need to submit the WBS form. However, if your group believes that individual contributions differ and you wish to assign marks accordingly, please complete the **WBS.xls** file (located in the same folder as the assignment document) and include it in your report. All members must agree on the contents of the WBS. In addition, the completed **WBS.xls** must be emailed to Dr. Li Fang **and** your TA, with all group members copied in the email.

## 6. DEMOSTRATION & PRESENTATION (Deadline: Week 14 Friday, 11:59pm)

Your group is required to present your work to your TA to demonstrate the working of the application – presenting ALL the required functionalities of the application. It is advised that you planned your demonstration in a story-boarding flow to facilitate understanding of your application. Please introduce your members and group number at the start of presentation, all the group members must take turn to present. The presenter should show his/her face while presenting.

In the production, you may include:
a) Explaining essential and relevant information about the application
b) Run-through and elaborate on essential part/s of your implementation/coding

*   The presentation duration must not exceed **15 minutes in total**.
*   The font size used must be large enough to be readable and viewable.
*   The demo of the application is to be done in **real-time and NOT pre-run display**.
*   The presentation can be conducted either through **online meeting or physically**.

**You will create your own test cases and data to test your application thoroughly.

## 7. THE DELIVERABLE (Deadline: One day before your scheduled oral presentation with your TA)

Your group submission should include the following:
a. The report (separate diagram file if diagram is unclear in report)
b. All implementation codes and java documentation (Javadoc).
c. Other relevant files (e.g. data files, setup instruction, etc.)

## 8. ASSESSMENT WEIGHTAGE

*   **UML Class Diagram [25 Marks]**
    *   Show mainly the Entity classes, the essential Control and Boundary classes, and enumeration type (if there is).
    *   Clarity, Correctness and Completeness of details and relationship.
*   **UML Sequence Diagram [20 Marks]**
    *   Show only the Sequence Diagram mentioned in 5(b)
    *   Clarity, Correctness and Completeness of flow and object interactions details (Boundary-Control-Entities)
*   **Design Consideration [15 Marks]**
    *   Usage of OO concepts and principle - correctness and appropriateness
    *   Explanation of design choices and how it fits the project requirements
    *   Coupling and cohesion of classes
*   **Implementation Code [20 Marks]**
    *   Diagram to Code correctness, readability, Java naming convention, exception handling, completeness of Java Doc and overall quality.
    *   Creativity of the additional features/functionality added to the system.
    *   A Java API HTML documentation of ALL your defined classes using Javadoc must be submitted. The use of javadoc feature is documented in Appendix D.
*   **Demonstration and report [20 Marks]**
    *   Coverage of application essentials and functionalities, user friendliness, demo flow, innovation.
    *   Report structure and reflection
    *   Highlight clearly any **additional features** implemented in the system.

## 9. SUBMISSION

This is a **group assignment**, and one submission from each group.
Report format guidelines are provided in the Appendix C below.

1.  Soft copy of your deliverables to be **uploaded** to your individual SC/CE/CZ2002 **LAB site** (e.g. FEP1, FSP1, etc.) in **NTULearn**. The link is provided on the left panel "Assignment Submission".
    File name convention : `<lab_grp>-grp<assignment_grp#>.<ext>` E.g., FEP2-grp3.pdf `[<ext.>]` can be pdf, doc, zip,]
2.  **DEADLINE**: One day before your scheduled oral presentation with your TA.

**Important:**
Note that **THREE (3) marks will be deducted for the delay submission of each calendar day**. Lateness is based on the date the captured in NTULearn or subsequent resubmissions (whichever is later). So **check your work before submitting**.

## 10. REFERENCES & TOOLS

*   **UML Diagrams tool - Visual Paradigm**
    *   http://www.visual-paradigm.com/
    *   http://www.visual-paradigm.com/support/documents/vpuserguide/94/2576/7190_drawingclass.html
*   **NTULearn Cx2002 main course site content**
*   **NTULearn Cx2002 course site content on “File Input/Output"**
*   **Object Serialization tutorial**
    *   http://www.javabeginner.com/uncategorized/javaserialization
*   **Windows Media Encoder (a suggestion)**
    *   http://www.microsoft.com/expression/products/EncoderPro_Overview.aspx
    *   [You can also try with the video recording feature for gaming in Windows 10 – press 'Windows key + G' ]

---

## APPENDIX A:

You may refer to the list of sample test cases below as a guide for your testing and demo video. Depending on your design and user-friendliness of your data entries process, there may be multiple steps taken.

The test cases provided are intended as examples. You are encouraged to develop your own test cases to thoroughly validate your system.

| No. | Test Cases                                                   | Expected Behavior                                                                                                                                                           | Failure Indicators                                                                                                                                                           |
|-----|--------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| 1   | Valid User Login                                             | User should be able to access their dashboard based on their roles                                                                                                          | User cannot log in or receive incorrect error messages                                                                                                                       |
| 2   | Invalid ID                                                   | User receives a notification about incorrect ID                                                                                                                             | User is allowed to log in with an invalid ID or fail to provide meaningful error message                                                                                   |
| 3   | Incorrect Password                                           | System should deny access and alert the user to incorrect password                                                                                                          | User logs in successfully with a wrong password or fail to provide meaningful error message                                                                                |
| 4   | Password Change Functionality                                | System updates password, prompt re-login and allows login with new credentials                                                                                              | System does not update the password or denies access with the new password                                                                                                   |
| 5.  | Company Representative Account Creation                      | A new Company Representative should only be able to log in to their account after it has been approved by a Career Center Staff                                            | Company Representative staff can log in without any authorization                                                                                                            |
| 6.  | Internship Opportunity Visibility Based on User Profile and Toggle | Internship opportunities are visible to students based on their year of study, major, internship level eligibility, and the visibility setting                                | Students see internship opportunities not relevant to their profile (wrong major, wrong level for their year) or when visibility is off                                      |
| 7.  | Internship Application Eligibility                           | Students can only apply for internship opportunities relevant to their profile (correct major preference, appropriate level for their year of study) and when visibility is on | Students can apply for internship opportunities not relevant to their profile (wrong major preference, Basic-level students applying for Intermediate/Advanced opportunities) or when visibility is off |
| 8.  | Viewing Application Status after Visibility Toggle Off       | Students continue to have access to their application details regardless of internship opportunities' visibility.                                                              | Application details become inaccessible once visibility is off.                                                                                                              |
| 10  | Single Internship Placement Acceptance per Student           | System allows accepting one internship placement and automatically withdraws all other applications once a placement is accepted                                            | Student can accept more than one internship placement, or other applications remain active after accepting a placement                                                       |
| 13  | Company Representative Internship Opportunity Creation       | System allows Company Representatives to create internship opportunities only when they meet system requirements                                                              | System allows creation of opportunities with invalid data or exceeds maximum allowed opportunities per representative                                                        |
| 14  | Internship Opportunity Approval Status                       | Company Representatives can view pending, approved, or rejected status updates for their submitted opportunities                                                              | Status updates are not visible, incorrect, or not properly saved in the system                                                                                               |
| 15  | Internship Detail Access for Company Representative          | Company Representatives can always access full details of internship opportunities they created, regardless of visibility setting                                           | Opportunity details become inaccessible when visibility is toggled off for their own opportunities                                                                         |
| 16  | Restriction on Editing Approved Opportunities                | Edit functionality is restricted for Company Representatives once internship opportunities are approved by Career Center Staff                                             | Company Representatives are able to make changes to opportunity details after approval                                                                                     |
| 18  | Student Application Management and Placement Confirmation    | Company Representatives retrieve correct student applications, update slot availability accurately, and correctly confirm placement details                                 | Incorrect application retrieval, slot counts not updating properly, or failure to reflect placement confirmation details accurately                                        |
| 19  | Internship Placement Confirmation Status Update              | Placement confirmation status is updated to reflect the actual confirmation condition                                                                                       | System fails to update or incorrectly records the placement confirmation status                                                                                              |
| 20  | Create, Edit, and Delete Internship Opportunity Listings     | Company Representatives should be able to add new opportunities, modify existing opportunity details (before approval by Career Center Staff), and remove opportunities from the system | Inability to create, edit, or delete opportunities or errors during these operations                                                                                         |
| 21  | Career Center Staff Internship Opportunity Approval          | Career Center Staff can review and approve/reject internship opportunities submitted by Company Representatives                                                               | Career Center Staff cannot access submitted opportunities for review, approval/rejection actions fail to update opportunity status, or approved opportunities do not become visible to students as expected |
| 22  | Toggle Internship Opportunity Visibility                     | Changes in visibility should be reflected accurately in the internship opportunity list visible to students                                                                 | Visibility settings do not update or do not affect the opportunity listing as expected                                                                                       |
| 23  | Career Center Staff Internship Opportunity Management        | Withdrawal approvals and rejections are processed correctly, with system updates to reflect the decision and slot availability changes                                        | Incorrect or failed processing of withdrawal requests or slot counts not updating properly                                                                                   |
| 24  | Generate and Filter Internship Opportunities                 | Accurate report generation with options to filter by placement status, major, company, level, and other specified categorie                                                   | Reports are inaccurate, incomplete, or filtering does not work as expected                                                                                                   |

---

## APPENDIX B:

**Declaration of Original Work for SC2002 Assignment**

We hereby declare that the attached group assignment has been researched, undertaken, completed, and submitted as a collective effort by the group members listed below.

We have honored the principles of academic integrity and have upheld Student Code of Academic Conduct in the completion of this work.

We understand that if plagiarism is found in the assignment, then lower marks or no marks will be awarded for the assessed work. In addition, disciplinary actions may be taken.

| Name | Course | Lab Group | Signature / Date |
|--------|--------|-----------|------------------|
|      |        |           |                  |
|      |        |           |                  |
|      |        |           |                  |
|      |        |           |                  |

**Important notes:**
1.  Name must **EXACTLY MATCH** the one printed on your Matriculation Card.
2.  Student Code of Academic Conduct includes the latest guidelines on usage of Generative AI and any other guidelines as released by NTU.

---

## APPENDIX C:

**Report requirement**

1.  **Format:**
    For the main content, please use Times New Roman 12 pt font size and 1.5 line spacing. You may choose to use other fonts (e.g., Courier New) for code segments. Please use the following report structure:
    *   Cover page: Declaration of original work (Appendix B)
    *   Design Considerations
        *   Approach taken, Principles used, Assumptions made, etc.
        *   **Optional:** You can show the important code segment (e.g., a method or a few lines of code) and necessary illustrations to explain your solution.
    *   Detailed UML Class Diagram.
        *   Further Notes, if needed
    *   Detailed UML Sequence Diagram of stated function.
        *   Further Notes, if needed
    *   Testing.
        *   Test Cases and Results
    *   Reflection.
        *   The difficulties encountered and the way to conquer, the knowledge learnt from this course, further improvement suggestion.

2.  **Length:**
    The report should be at most 12 pages from cover to cover including diagrams/Testing results/references/appendix, if there is any. If you could well present your work in fewer than 12 pages, you are encouraged to do so.

    DO NOT include source code in the report but instead store the source code in a folder. You are to ensure that the diagrams are readable and clear to the reader. [You can save the diagrams as image files and include in a folder]

---

## APPENDIX D:

**Creating Javadoc**

For detailed information, refer to the official documentation:
*   [Oracle Javadoc Guide](https)

For Eclipse users:
*   [YouTube Tutorial - Using Javadoc in Eclipse](https)

**Example Output**

The generated documentation can be viewed by opening the index.html file in the output folder.

**Using Javadoc from Command Prompt**

Steps to generate API documentation:
1.  **Locate the installed path of JDK (Java Development Kit)**
    *   For Windows, it's typically: `C:\Program Files\Java\jdk<version>\`
2.  **Open Command Prompt**
3.  **Navigate to your src directory**
    Use the `cd` command to change directory.
4.  **Run the Javadoc command**
    `<path-to-jdk>\bin\javadoc -d ./html -author -private -noqualifier all -version <package1> <package2> ...`

    **Example:**
    `C:\subject\2024sem2\sc2002\src> "C:\Program Files\Java\jdk1.8.0_05\bin\javadoc" ^ -d ./html -author -private -noqualifier all -version edu.ntu.ccds.sc2002`

**Explanation of Command Components**

| Statement                                       | Purpose                                                              |
|-------------------------------------------------|----------------------------------------------------------------------|
| `C:\subject\2024sem2\sc2002\src>`                | Your source root directory                                           |
| `"C:\Program Files\Java\jdk1.8.0_05\bin\javadoc"` | Path to javadoc.exe. Use double quotes if path contains spaces       |
| `-d ./html`                                     | Output directory for generated HTML docs (creates html folder in current directory) |
| `-author`                                       | Includes @author tag (if used in source)                             |
| `-private`                                      | Includes documentation for private fields and methods                |
| `-noqualifier all`                              | Omits full package qualifiers (e.g., shows String instead of java.lang.String) |
| `-version`                                      | Includes @version tag (if used in source)                            |
| `edu.ntu.ccds.sc2002`                           | Your Java package(s) to document                                     |
