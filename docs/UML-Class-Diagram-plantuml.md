# UML Class Diagram

```plantuml
@startuml SC2002_Class_Diagram

skinparam classAttributeIconSize 0

' Enumerations
enum UserRole {
    STUDENT
    COMPANY_REPRESENTATIVE
    CAREER_CENTER_STAFF
}

enum InternshipLevel {
    BASIC
    INTERMEDIATE
    ADVANCED
}

enum InternshipStatus {
    PENDING
    APPROVED
    REJECTED
    FILLED
}

enum ApplicationStatus {
    PENDING
    SUCCESSFUL
    UNSUCCESSFUL
    CONFIRMED
}

enum ApprovalStatus {
    PENDING
    APPROVED
    REJECTED
}

enum SortCriteria {
    ALPHABETICAL
    OPENING_DATE
    CLOSING_DATE
    LEVEL
}

enum PasswordChangeResult {
    SUCCESS
    MISMATCH
    INVALID_FORMAT
    DUPLICATE
    INCORRECT_OLD
}

' Entity Classes
abstract class User {
    -userId : String
    -password : String
    -name : String
    -email : String
    -role : UserRole
    -firstLogin : boolean
    +getters/setters()
    +{abstract} getProfileInfo() : String
}

class Student {
    -yearOfStudy : int
    -major : String
    -confirmedPlacementId : String
    +getters/setters()
    +hasConfirmedPlacement() : boolean
    +getProfileInfo() : String
}

class CompanyRepresentative {
    -companyName : String
    -department : String
    -position : String
    -approvalStatus : ApprovalStatus
    +getters/setters()
    +isApproved() : boolean
    +getProfileInfo() : String
}

class CareerCenterStaff {
    -staffDepartment : String
    +getters/setters()
    +getProfileInfo() : String
}

class Internship {
    -internshipId : String
    -title : String
    -description : String
    -level : InternshipLevel
    -preferredMajor : String
    -openingDate : LocalDate
    -closingDate : LocalDate
    -status : InternshipStatus
    -companyName : String
    -representativeId : String
    -totalSlots : int
    -confirmedSlots : int
    -visible : boolean
    +getters/setters()
    +incrementConfirmedSlots() : void
    +decrementConfirmedSlots() : void
    +hasAvailableSlots() : boolean
    +isApproved() : boolean
    +isFilled() : boolean
}

class Application {
    -applicationId : String
    -studentId : String
    -internshipId : String
    -status : ApplicationStatus
    -applicationDate : LocalDateTime
    +getters/setters()
}

class WithdrawalRequest {
    -requestId : String
    -studentId : String
    -applicationId : String
    -reason : String
    -status : ApprovalStatus
    -requestDate : LocalDateTime
    -reviewedByStaffId : String
    -reviewDate : LocalDateTime
    +getters/setters()
}

class FilterSettings {
    -userId : String
    -levelFilter : InternshipLevel
    -majorFilter : String
    -statusFilter : InternshipStatus
    -sortBy : SortCriteria
    +getters/setters()
    +getComparator() : Comparator<Internship>
}

' Boundary Classes
abstract class MenuInterface <<boundary>> {
    #scanner : Scanner
    +{abstract} displayMenu() : void
    +{abstract} handleMenuChoice(choice : int) : void
    #pause() : void
    #clearScreen() : void
    #printHeader(title : String) : void
    #getIntInput(prompt : String) : int
    #getStringInput(prompt : String) : String
}

class LoginMenu <<boundary>> {
    -authController : AuthenticationController
    +displayMenu() : void
    +handleMenuChoice(choice : int) : void
}

class StudentMenu <<boundary>> {
    -authController : AuthenticationController
    -student : Student
    +displayMenu() : void
    +handleMenuChoice(choice : int) : void
}

class CompanyRepresentativeMenu <<boundary>> {
    -authController : AuthenticationController
    -representative : CompanyRepresentative
    +displayMenu() : void
    +handleMenuChoice(choice : int) : void
}

class CareerCenterStaffMenu <<boundary>> {
    -authController : AuthenticationController
    -staff : CareerCenterStaff
    +displayMenu() : void
    +handleMenuChoice(choice : int) : void
}

' Control Classes
class UserManager <<control>> <<singleton>> {
    -users : List<User>
    -{static} instance : UserManager
    +{static} getInstance() : UserManager
    +authenticateUser(userId : String, password : String) : User
    +userExists(userId : String) : boolean
    +addUser(user : User) : void
    +getUserById(userId : String) : User
    +updateUser(user : User) : void
    +getPendingRepresentatives() : List<CompanyRepresentative>
    +getAllStudents() : List<Student>
    +reviewRepresentative(rep : CompanyRepresentative, choice : int) : void
    +loadUsersFromCSV(filePath : String, role : UserRole) : void
    +saveUsers() : void
}

class InternshipManager <<control>> <<singleton>> {
    -internships : List<Internship>
    -{static} instance : InternshipManager
    +{static} getInstance() : InternshipManager
    +addInternship(...) : void
    +getInternshipById(id : String) : Internship
    +updateInternship(internship : Internship) : void
    +getInternshipsByRepresentative(repId : String) : List<Internship>
    +getVisibleInternshipsForStudent(student : Student) : List<Internship>
    +getPendingInternships() : List<Internship>
    +filterInternships(list : List, settings : FilterSettings) : List<Internship>
    +countInternshipsByRepresentative(repId : String) : int
    +reviewInternship(internship : Internship, choice : int) : void
    +editInternshipField(internship : Internship, field : int, value : String) : void
    +toggleInternshipVisibility(internship : Internship) : void
    +getAllInternships() : List<Internship>
    +getAllInternships(classifier : Function) : Map
    +saveInternships() : void
}

class ApplicationManager <<control>> <<singleton>> {
    -applications : List<Application>
    -{static} instance : ApplicationManager
    +{static} getInstance() : ApplicationManager
    +addApplication(appId : String, studentId : String, internshipId : String) : boolean
    +getApplicationById(id : String) : Application
    +updateApplication(application : Application) : void
    +removeApplication(application : Application) : void
    +getApplicationsByStudent(studentId : String) : List<Application>
    +getApplicationsByInternship(internshipId : String) : List<Application>
    +countPendingApplicationsByStudent(studentId : String) : int
    +hasAppliedToInternship(studentId : String, internshipId : String) : boolean
    +reviewApplication(app : Application, choice : int) : void
    +getApplicationsByInternshipandStatus(id : String, status : ApplicationStatus) : List<Application>
    +getApplicationsByStudentandStatus(id : String, status : ApplicationStatus) : List<Application>
    +getApplicationCount(list : List, status : ApplicationStatus) : long
    +handleApplicationAcceptance(student : Student, app : Application) : void
    +saveApplications() : void
}

class WithdrawalManager <<control>> <<singleton>> {
    -withdrawalRequests : List<WithdrawalRequest>
    -{static} instance : WithdrawalManager
    +{static} getInstance() : WithdrawalManager
    +addWithdrawalRequest(...) : void
    +getWithdrawalById(id : String) : WithdrawalRequest
    +updateWithdrawalRequest(request : WithdrawalRequest) : void
    +getPendingWithdrawals() : List<WithdrawalRequest>
    +getWithdrawalsByStudent(studentId : String) : List<WithdrawalRequest>
    +hasPendingWithdrawal(applicationId : String) : boolean
    +reviewWithdrawal(request : WithdrawalRequest, staffId : String, choice : int) : void
    +saveWithdrawals() : void
}

class FilterManager <<control>> <<singleton>> {
    -userFilters : Map<String, FilterSettings>
    -{static} instance : FilterManager
    +{static} getInstance() : FilterManager
    +getFilterSettings(userId : String) : FilterSettings
    +updateFilterSettings(settings : FilterSettings) : void
    +saveFilters() : void
}

class AuthenticationController <<control>> {
    -userManager : UserManager
    -currentUser : User
    +login(userId : String, password : String) : boolean
    +logout() : void
    +getCurrentUser() : User
    +attemptPasswordChange(...) : PasswordChangeResult
    +registerCompanyRepresentative(...) : boolean
    +userExists(userId : String) : boolean
    -changePassword(userId : String, newPassword : String) : boolean
}

' Utility Classes
class FileManager <<utility>> {
    +{static} saveToFile(filePath : String, data : List) : void
    +{static} loadFromFile(filePath : String) : List
    +{static} readCSV(filePath : String, hasHeader : boolean) : List<String[]>
    +{static} ensureDataDirectory() : void
    +{static} loadIdProperties(filePath : String) : Properties
    +{static} saveIdProperties(props : Properties, filePath : String, comment : String) : void
}

class InputValidator <<utility>> {
    +{static} isValidEmail(email : String) : boolean
    +{static} isValidPassword(password : String) : boolean
    +{static} isValidDate(date : String) : boolean
    +{static} isPositiveInteger(input : String) : boolean
    +{static} isValidRange(value : int, min : int, max : int) : boolean
}

class IdGenerator <<utility>> {
    +{static} generateInternshipId() : String
    +{static} generateApplicationId() : String
    +{static} generateWithdrawalId() : String
}

class DateUtils <<utility>> {
    +{static} FORMATTER : DateTimeFormatter
    +{static} parseDate(dateStr : String) : LocalDate
    +{static} formatDate(date : LocalDate) : String
}

' Inheritance Relationships
User <|-- Student
User <|-- CompanyRepresentative
User <|-- CareerCenterStaff
MenuInterface <|-- LoginMenu
MenuInterface <|-- StudentMenu
MenuInterface <|-- CompanyRepresentativeMenu
MenuInterface <|-- CareerCenterStaffMenu

' Associations - Entity Relationships
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

' Control Layer Associations
UserManager "1" --> "*" User : manages
InternshipManager "1" --> "*" Internship : manages
ApplicationManager "1" --> "*" Application : manages
WithdrawalManager "1" --> "*" WithdrawalRequest : manages
FilterManager "1" --> "*" FilterSettings : manages
AuthenticationController --> UserManager : uses
AuthenticationController --> User : manages current

' Boundary Layer Associations
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

' Utility Associations (Dependencies)
UserManager ..> FileManager : uses
InternshipManager ..> FileManager : uses
ApplicationManager ..> FileManager : uses
WithdrawalManager ..> FileManager : uses
FilterManager ..> FileManager : uses
CompanyRepresentativeMenu ..> InputValidator : uses
LoginMenu ..> InputValidator : uses
StudentMenu ..> InputValidator : uses
CompanyRepresentativeMenu ..> IdGenerator : uses
StudentMenu ..> IdGenerator : uses
CompanyRepresentativeMenu ..> DateUtils : uses
CareerCenterStaffMenu ..> DateUtils : uses
InputValidator ..> DateUtils : uses
IdGenerator ..> FileManager : uses
AuthenticationController --> PasswordChangeResult : returns

@enduml
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
