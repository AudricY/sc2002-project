# Internship Placement Management System Requirements

## Software Requirements
- Centralized CLI hub for Students, Company Representatives, and Career Center Staff; all users must log in with default password `password` unless changed.
- Accepts initialization via uploaded user list file; students and career center staff are preloaded, company representatives self-register and await staff approval.
- User profile data: Student (Year of Study, Major), Company Representative (Company Name, Department, Position), Career Center Staff (Staff Department).
- Supports user management for login, logout, and password changes for every user.
- Prohibits use of databases, JSON, or XML; relies on files for data storage.
- Saves user-specific internship list filter settings across menu navigation; default sort is alphabetical.

### Student Capabilities
- Automatically registered from the student list file; may view internships matching year, major, and visibility.
- Limited to three concurrent internship applications; Years 1–2 restricted to Basic-level internships, Years 3–4 eligible for all levels.
- May view their applied internships regardless of visibility toggles and track statuses (`Pending`, `Successful`, `Unsuccessful`).
- Upon `Successful` status, can accept exactly one placement; acceptance withdraws remaining applications.
- Can request withdrawal of applications before or after placement confirmation, subject to Career Center Staff approval.

### Company Representative Capabilities
- Begin with empty representative list; must register under a company and access the system only after Career Center Staff approval.
- May create up to five internship opportunities per representative with required details: title, description, level (Basic/Intermediate/Advanced), preferred major, opening date, closing date, status (`Pending`, `Approved`, `Rejected`, `Filled`), company name, assigned representative, and up to ten slots.
- Can view internship application details and associated student information for their opportunities.
- Authorized to approve or reject student applications; approvals set student status to `Successful` and contribute to filling slots until status becomes `Filled` once all slots are confirmed by students.
- May toggle internship visibility on or off; toggles immediately affect student listings but representatives retain full access.
- Editing opportunities is restricted once Career Center Staff approve them.

### Career Center Staff Capabilities
- Automatically registered from the staff list file and empowered to authorize or reject company representative accounts.
- Review and decide on internship opportunity approvals; approved opportunities become visible to eligible students.
- Process and decide on student withdrawal requests both before and after placement confirmation, ensuring slot counts stay accurate.
- Generate reports on internship opportunities with filters for status, preferred majors, internship level, and related criteria.

## Required Diagrams and Documentation
- **UML Class Diagram**: Detailed diagram covering entity, essential control, boundary classes, and enumerations; must clearly show relationships, notations, and annotate OO principle applications.
- **UML Sequence Diagram**: Detailed sequence for the company representative’s internship management and application review flow, showing relevant objects, interaction fragments, and sufficient detail.
- **Design Considerations Write-Up**: Discuss approach, OO concepts, principles, assumptions, extensibility, maintainability, trade-offs, and rationale for chosen design patterns versus alternatives.
- **Additional Features Summary**: Explicitly highlight any extra functionality implemented beyond mandatory requirements.
- **Testing Documentation**: Provide test cases and results demonstrating thorough system validation.
- **Reflection Section**: Describe encountered difficulties, resolutions, lessons learned, and suggestions for further improvement.
- **Javadoc Output**: Generate comprehensive Java API documentation (HTML) for all defined classes.