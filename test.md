```mermaid
%% --------------------
%% CLASS DIAGRAM (fixed syntax)
%% --------------------
classDiagram
    class User {
        - String userId
        - String password
        - String name
        - String email
        - UserRole role
        + login()
        + logout()
    }

    class Student {
        - String studentId
        - String matricNo
        - String course
        - List<String> skills
        + applyToInternship(internshipId)
        + withdrawApplication(applicationId)
    }

    class CompanyRepresentative {
        - String repId
        - String companyName
        - String department
        - String position
        - ApprovalStatus approvalStatus
        + createInternship(internshipData)
        + reviewApplication(applicationId, decision)
    }

    class CareerCenterStaff {
        - String staffId
        + approveCompanyRep(repId)
        + reviewWithdrawal(requestId)
        + overrideApplicationDecision(applicationId, newDecision)
    }

    class Internship {
        - String internshipId
        - String title
        - String description
        - InternshipLevel level
        - InternshipStatus status
        - Date openingDate
        - Date closingDate
        - String representativeId
        + isOpen()
    }

    class Application {
        - String applicationId
        - String studentId
        - String internshipId
        - ApplicationStatus status
        - DateTime applicationDate
        + withdraw()
    }

    class WithdrawalRequest {
        - String requestId
        - String studentId
        - String applicationId
        - String reason
        - ApprovalStatus status
    }

    class InternshipManager {
        + getInternshipsByRepresentative(repId)
        + getInternshipById(id)
        + saveInternship(internship)
    }

    class ApplicationManager {
        + addApplication(application)
        + getApplicationsByInternship(internshipId)
        + reviewApplication(applicationId, decision)
        + updateApplication(application)
    }

    class WithdrawalManager {
        + addWithdrawalRequest(request)
        + getWithdrawalById(id)
    }

    class UserManager {
        + getUserById(id)
    }

    class AuthenticationController {
        + login(credentials)
        + logout()
        + getCurrentUser()
    }

    class MenuInterface {
        + displayMenu()
    }

    class CompanyRepresentativeMenu {
        + displayMenu()
        + viewApplicationsForInternship(internshipId)
        + reviewApplication(applicationId, decision)
    }

    %% Relationships
    User <|-- Student
    User <|-- CompanyRepresentative
    User <|-- CareerCenterStaff

    CompanyRepresentative "1" o-- "*" Internship : manages
    Internship "1" -- "*" Application : receives
    Student "1" -- "*" Application : submits
    Application "1" -- "0..1" WithdrawalRequest : mayHave

    CompanyRepresentativeMenu --> AuthenticationController : uses
    CompanyRepresentativeMenu --> InternshipManager : queries
    CompanyRepresentativeMenu --> ApplicationManager : reviews

    %% Notes (fixed position)
    note for User "Encapsulation: private (-) attributes with getters/setters"
    note for Student "Inheritance + Polymorphism: Student extends User"
    note for InternshipManager "SRP + Singleton: centralizes internship handling"
