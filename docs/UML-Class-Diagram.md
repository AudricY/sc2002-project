# UML Class Diagram

```mermaid
classDiagram
    %% Enumerations
    class UserRole {
        <<enumeration>>
        STUDENT
        COMPANY_REPRESENTATIVE
        CAREER_CENTER_STAFF
    }

    class InternshipLevel {
        <<enumeration>>
        BASIC
        INTERMEDIATE
        ADVANCED
    }

    class InternshipStatus {
        <<enumeration>>
        PENDING
        APPROVED
        REJECTED
        FILLED
    }

    class ApplicationStatus {
        <<enumeration>>
        PENDING
        SUCCESSFUL
        UNSUCCESSFUL
    }

    class ApprovalStatus {
        <<enumeration>>
        PENDING
        APPROVED
        REJECTED
    }

    class SortCriteria {
        <<enumeration>>
        ALPHABETICAL
        OPENING_DATE
        CLOSING_DATE
        LEVEL
    }

    %% Entity Classes
    class User {
        <<abstract>>
        -String userId
        -String password
        -String name
        -String email
        -UserRole role
        -boolean firstLogin
        +getters/setters()
        +getProfileInfo()* String
    }

    class Student {
        -int yearOfStudy
        -String major
        -String confirmedPlacementId
        +getters/setters()
        +hasConfirmedPlacement() boolean
        +getProfileInfo() String
    }

    class CompanyRepresentative {
        -String companyName
        -String department
        -String position
        -ApprovalStatus approvalStatus
        +getters/setters()
        +isApproved() boolean
        +getProfileInfo() String
    }

    class CareerCenterStaff {
        -String staffDepartment
        +getters/setters()
        +getProfileInfo() String
    }

    class Internship {
        -String internshipId
        -String title
        -String description
        -InternshipLevel level
        -String preferredMajor
        -LocalDate openingDate
        -LocalDate closingDate
        -InternshipStatus status
        -String companyName
        -String representativeId
        -int totalSlots
        -int confirmedSlots
        -boolean visible
        +getters/setters()
        +incrementConfirmedSlots()
        +decrementConfirmedSlots()
        +hasAvailableSlots() boolean
    }

    class Application {
        -String applicationId
        -String studentId
        -String internshipId
        -ApplicationStatus status
        -LocalDateTime applicationDate
        -boolean confirmed
        +getters/setters()
    }

    class WithdrawalRequest {
        -String requestId
        -String studentId
        -String applicationId
        -String reason
        -ApprovalStatus status
        -LocalDateTime requestDate
        -String reviewedByStaffId
        -LocalDateTime reviewDate
        +getters/setters()
    }

    class FilterSettings {
        -String userId
        -InternshipLevel levelFilter
        -String majorFilter
        -InternshipStatus statusFilter
        -SortCriteria sortBy
        +getters/setters()
        +getComparator() Comparator~Internship~
    }

    %% Boundary Classes
    class MenuInterface {
        <<abstract>>
        <<boundary>>
        #Scanner scanner
        +displayMenu()*
        +handleMenuChoice(int)*
        #pause()
        #clearScreen()
        #getIntInput(String) int
        #getStringInput(String) String
    }

    class LoginMenu {
        <<boundary>>
        -AuthenticationController authController
        +displayMenu()
        +handleMenuChoice(int)
    }

    class StudentMenu {
        <<boundary>>
        -AuthenticationController authController
        -Student student
        +displayMenu()
        +handleMenuChoice(int)
    }

    class CompanyRepresentativeMenu {
        <<boundary>>
        -AuthenticationController authController
        -CompanyRepresentative representative
        +displayMenu()
        +handleMenuChoice(int)
    }

    class CareerCenterStaffMenu {
        <<boundary>>
        -AuthenticationController authController
        -CareerCenterStaff staff
        +displayMenu()
        +handleMenuChoice(int)
    }

    %% Control Classes
    class UserManager {
        <<control>>
        <<singleton>>
        -List~User~ users
        -UserManager instance
        +getInstance()$ UserManager
        +authenticateUser(String, String) User
        +addUser(User)
        +getUserById(String) User
        +updateUser(User)
        +getPendingRepresentatives() List
        +loadUsersFromCSV(String, UserRole)
        +saveUsers()
    }

    class InternshipManager {
        <<control>>
        <<singleton>>
        -List~Internship~ internships
        -InternshipManager instance
        +getInstance()$ InternshipManager
        +addInternship(Internship)
        +getInternshipById(String) Internship
        +updateInternship(Internship)
        +getVisibleInternshipsForStudent(Student) List
        +filterInternships(List, FilterSettings) List
        +saveInternships()
    }

    class ApplicationManager {
        <<control>>
        <<singleton>>
        -List~Application~ applications
        -ApplicationManager instance
        +getInstance()$ ApplicationManager
        +addApplication(Application)
        +updateApplication(Application)
        +getApplicationsByStudent(String) List
        +getApplicationsByInternship(String) List
        +countPendingApplicationsByStudent(String) int
        +saveApplications()
    }

    class WithdrawalManager {
        <<control>>
        <<singleton>>
        -List~WithdrawalRequest~ withdrawalRequests
        -WithdrawalManager instance
        +getInstance()$ WithdrawalManager
        +addWithdrawalRequest(WithdrawalRequest)
        +getPendingWithdrawals() List
        +updateWithdrawalRequest(WithdrawalRequest)
        +saveWithdrawals()
    }

    class FilterManager {
        <<control>>
        <<singleton>>
        -Map~String,FilterSettings~ userFilters
        -FilterManager instance
        +getInstance()$ FilterManager
        +getFilterSettings(String) FilterSettings
        +updateFilterSettings(FilterSettings)
        +saveFilters()
    }

    class AuthenticationController {
        <<control>>
        -UserManager userManager
        -User currentUser
        +login(String, String) boolean
        +logout()
        +getCurrentUser() User
        +changePassword(String, String) boolean
        +registerCompanyRepresentative(...) boolean
    }

    %% Utility Classes
    class FileManager {
        <<utility>>
        +saveToFile(String, List)$
        +loadFromFile(String)$ List
        +readCSV(String)$ List~String[]~
        +ensureDataDirectory()$
    }

    class InputValidator {
        <<utility>>
        +isValidEmail(String)$ boolean
        +isValidPassword(String)$ boolean
        +isValidDate(String)$ boolean
        +parseDate(String)$ LocalDate
        +isPositiveInteger(String)$ boolean
    }

    class IdGenerator {
        <<utility>>
        +generateUserId(String)$ String
        +generateInternshipId()$ String
        +generateApplicationId()$ String
        +generateWithdrawalId()$ String
    }

    %% Inheritance Relationships
    User <|-- Student
    User <|-- CompanyRepresentative
    User <|-- CareerCenterStaff
    MenuInterface <|-- LoginMenu
    MenuInterface <|-- StudentMenu
    MenuInterface <|-- CompanyRepresentativeMenu
    MenuInterface <|-- CareerCenterStaffMenu

    %% Associations - Entity Relationships
    User --> UserRole : has
    Student "1" --> "0..*" Application : creates
    CompanyRepresentative "1" --> "0..5" Internship : creates
    Internship --> InternshipLevel : has
    Internship --> InternshipStatus : has
    Internship "1" --> "0..*" Application : receives
    Application --> ApplicationStatus : has
    Application "1" --> "0..1" WithdrawalRequest : subject of
    WithdrawalRequest --> ApprovalStatus : has
    FilterSettings --> InternshipLevel : filters by
    FilterSettings --> InternshipStatus : filters by
    FilterSettings --> SortCriteria : sorts by

    %% Control Layer Associations
    UserManager "1" --> "*" User : manages
    InternshipManager "1" --> "*" Internship : manages
    ApplicationManager "1" --> "*" Application : manages
    WithdrawalManager "1" --> "*" WithdrawalRequest : manages
    FilterManager "1" --> "*" FilterSettings : manages
    AuthenticationController --> UserManager : uses
    AuthenticationController --> User : manages current

    %% Boundary Layer Associations
    LoginMenu --> AuthenticationController : uses
    StudentMenu --> AuthenticationController : uses
    StudentMenu --> InternshipManager : uses
    StudentMenu --> ApplicationManager : uses
    StudentMenu --> WithdrawalManager : uses
    StudentMenu --> FilterManager : uses
    CompanyRepresentativeMenu --> AuthenticationController : uses
    CompanyRepresentativeMenu --> InternshipManager : uses
    CompanyRepresentativeMenu --> ApplicationManager : uses
    CompanyRepresentativeMenu --> UserManager : uses
    CareerCenterStaffMenu --> AuthenticationController : uses
    CareerCenterStaffMenu --> UserManager : uses
    CareerCenterStaffMenu --> InternshipManager : uses
    CareerCenterStaffMenu --> ApplicationManager : uses
    CareerCenterStaffMenu --> WithdrawalManager : uses
    CareerCenterStaffMenu --> FilterManager : uses

    %% Utility Associations
    UserManager ..> FileManager : uses
    InternshipManager ..> FileManager : uses
    ApplicationManager ..> FileManager : uses
    WithdrawalManager ..> FileManager : uses
    FilterManager ..> FileManager : uses
    CompanyRepresentativeMenu ..> InputValidator : uses
    LoginMenu ..> InputValidator : uses
    CompanyRepresentativeMenu ..> IdGenerator : uses
    StudentMenu ..> IdGenerator : uses
    AuthenticationController ..> IdGenerator : uses
```

## OO Principles Applied

1. **Inheritance**: User hierarchy (Student, CompanyRepresentative, CareerCenterStaff extend User)
2. **Polymorphism**: Overridden getProfileInfo() for role-specific information, Menu implementations
3. **Encapsulation**: Private fields with public getters/setters, business logic encapsulated in methods
4. **Abstraction**: Abstract User and MenuInterface classes define contracts
5. **Composition**: Managers composed into boundary classes rather than inherited
6. **Singleton Pattern**: All Manager classes ensure single instance (getInstance())
7. **Separation of Concerns**: Entity-Control-Boundary architecture with clear layer responsibilities

## Key Relationships

- **1:N Associations**: One manager manages many entities (UserManager manages Users, etc.)
- **Inheritance**: Hierarchical class structures for User and MenuInterface
- **Dependencies**: Boundary classes depend on control layer, control layer depends on entity layer
- **Multiplicity**: Student creates 0..* Applications, CompanyRep creates 0..5 Internships
- **Aggregation**: FilterSettings and enumerations represent value objects used by entities
