# Design Considerations

## 1. Design Approach

### Architecture Pattern: Entity-Control-Boundary (ECB)

The system adopts the ECB architecture pattern, which provides clear separation of concerns:

- **Entity Layer** (`entity/`): Contains all domain objects and enumerations representing the core business data
- **Control Layer** (`control/`): Houses business logic, managers, and controllers that orchestrate operations
- **Boundary Layer** (`boundary/`): Manages user interaction through CLI menus and input/output handling

This architecture facilitates maintainability by isolating business logic from presentation concerns and data structures.

### File-Based Persistence Strategy

The system uses Java serialization for data persistence, storing objects in `.dat` files. This approach:
- Eliminates external database dependencies as required
- Preserves object relationships and complex data structures
- Provides simple save/load operations through `FileManager`
- Maintains data integrity through atomic file operations

CSV files serve as the initial data source for students and staff, processed only during system initialization.

## 2. Object-Oriented Concepts Applied

### Inheritance and Polymorphism

**User Hierarchy**: The abstract `User` class establishes a common contract for all user types, with concrete implementations (`Student`, `CompanyRepresentative`, `CareerCenterStaff`) providing specialized behavior.

```
User (abstract)
├── Student
├── CompanyRepresentative
└── CareerCenterStaff
```

Each subclass overrides `getProfileInfo()` to return role-specific information, demonstrating polymorphism in action.

**Menu Hierarchy**: `MenuInterface` provides template methods and common utilities, with specialized menu classes implementing role-specific interfaces.

### Encapsulation

All entity classes employ private fields with controlled access through public getters/setters. Business rules are enforced within entity methods:
- `Internship.incrementConfirmedSlots()` automatically sets status to `FILLED` when capacity is reached
- `Student.hasConfirmedPlacement()` abstracts null checking logic
- Password changes are managed exclusively through `AuthenticationController`

### Abstraction

Abstract classes and interfaces hide implementation details:
- `User` defines the contract without specifying user type details
- `MenuInterface` provides common menu operations without dictating specific workflows
- Manager classes abstract data access patterns

### Composition over Inheritance

Managers are composed into boundary classes rather than inherited:
- `StudentMenu` contains instances of `InternshipManager`, `ApplicationManager`, etc.
- This enables flexible recombination of functionality without deep inheritance hierarchies

## 3. Design Principles

### Single Responsibility Principle (SRP)

Each class has a single, well-defined responsibility:
- `FileManager`: File I/O operations only
- `InputValidator`: Input validation logic only
- `IdGenerator`: Unique ID generation only
- `AuthenticationController`: User authentication and session management

### Open/Closed Principle (OCP)

The system is open for extension but closed for modification:
- New user types can be added by extending `User` without modifying existing code
- New menu options require only new methods, not structural changes
- Filter criteria can be extended through `FilterSettings.SortCriteria` enum

### Dependency Inversion Principle (DIP)

High-level modules depend on abstractions:
- Menu classes depend on `AuthenticationController` interface, not concrete user implementations
- Managers work with abstract `User` types, not specific subclasses where possible

### Don't Repeat Yourself (DRY)

Common functionality is centralized:
- `MenuInterface` provides shared UI utilities (pause, clear, input prompts)
- `FileManager` handles all serialization operations
- `IdGenerator` centralizes ID creation logic

## 4. Design Patterns

### Singleton Pattern

All manager classes implement the Singleton pattern to ensure:
- Single source of truth for data collections
- Consistent state across the application
- Centralized data management and persistence

Implementation uses lazy initialization with `getInstance()` methods.

### Template Method Pattern

`MenuInterface` employs the template method pattern:
- Abstract methods `displayMenu()` and `handleMenuChoice()` define the workflow
- Subclasses provide specific implementations
- Common operations (pause, input handling) are inherited

### Factory-like Pattern

`IdGenerator` provides factory-style methods for creating unique identifiers, abstracting the generation strategy from consumers.

### State Pattern (Implicit)

Enumerations (`ApplicationStatus`, `InternshipStatus`, `ApprovalStatus`) represent state, with state transitions enforced through business logic in managers and entities.

## 5. Key Assumptions

1. **Single-User Session**: The system assumes one active user at a time with sequential access
2. **Data Integrity**: File operations are atomic; no concurrent access protection is implemented
3. **Input Trust**: Users provide well-formed input after validation prompts
4. **ID Uniqueness**: Counter-based ID generation assumes no manual data file manipulation
5. **CSV Format**: Initial CSV files follow strict format: comma-separated without escaping
6. **Approval Workflow**: All company representatives and internships require staff approval before activation

## 6. Extensibility and Maintainability

### Extensibility Features

1. **New User Types**: Add by extending `User` and creating corresponding menu class
2. **Additional Filters**: Extend `FilterSettings.SortCriteria` enum and add logic to `getComparator()`
3. **New Report Types**: Add methods to `CareerCenterStaffMenu` without affecting existing reports
4. **Enhanced Validation**: Add validators to `InputValidator` without modifying consumers

### Maintainability Features

1. **Centralized Constants**: File names defined as constants in manager classes
2. **Consistent Naming**: Classes follow role-responsibility naming (e.g., `UserManager`, `InternshipManager`)
3. **Clear Package Structure**: Logical grouping by architectural layer
4. **Minimal Dependencies**: Each class depends only on necessary components

### Testing Considerations

The design facilitates testing through:
- Manager singletons can be reset between tests (if reset methods added)
- Business logic is concentrated in manager and entity classes
- Input validation is separated from business logic
- File operations are isolated in `FileManager`

## 7. Trade-offs and Rationale

### Singleton Managers vs Dependency Injection

**Decision**: Singleton pattern for all managers

**Rationale**:
- Simpler for CLI application without DI framework
- Ensures consistent data state across menu transitions
- Reduces parameter passing complexity in menu constructors

**Trade-off**: Testing is more complex, but acceptable for assignment scope

### Serialization vs Custom File Format

**Decision**: Java serialization for persistence

**Rationale**:
- Preserves object graphs automatically
- Reduces boilerplate code for data conversion
- Meets "no database/JSON/XML" requirement elegantly

**Trade-off**: Files are not human-readable, but system includes CSV import for initial setup

### In-Memory Data Management

**Decision**: Load all data into memory at startup

**Rationale**:
- Simplifies data access patterns
- Acceptable performance for expected data volumes
- Eliminates need for query language or indexing

**Trade-off**: Not scalable to large datasets, but appropriate for university internship system

### Menu-Based Navigation vs Command Pattern

**Decision**: Hierarchical menu structure with switch statements

**Rationale**:
- Natural fit for CLI interface
- Intuitive user experience
- Simple to implement and maintain

**Trade-off**: Less flexible than command pattern, but sufficient for defined requirements

### Filter Persistence

**Decision**: Save user-specific filter settings to file

**Rationale**:
- Requirement states "saves user-specific internship list filter settings across menu navigation"
- Enhances user experience by remembering preferences
- Demonstrates understanding of state persistence

**Trade-off**: Additional file I/O, but negligible impact given data volumes

## 8. Security Considerations

While security is not a primary focus, the system implements basic measures:

1. **Password Storage**: Stored in plain text (acceptable for educational project; production would use hashing)
2. **Authentication Check**: Company representatives must be approved before access
3. **Authorization**: Menu access restricted by user role
4. **First Login**: Forces password change on initial login

## 9. Alternative Approaches Considered

### Alternative 1: Database-Backed Storage

**Rejected Because**: Requirements explicitly prohibit databases. Would provide better scalability and query capabilities but violate constraints.

### Alternative 2: JSON/XML for Data Files

**Rejected Because**: Requirements prohibit these formats. Would offer human-readable files but fail requirements.

### Alternative 3: Command-Line Arguments Instead of Menus

**Rejected Because**: Requirements specify "Centralized CLI hub" implying interactive sessions, not one-shot commands.

### Alternative 4: Multi-threaded Access

**Not Implemented**: Single-user assumption makes concurrency unnecessary. Would add significant complexity for no benefit.

## 10. Future Enhancements

Potential improvements while maintaining current architecture:

1. **Notification System**: Alert students when applications are approved
2. **Search Functionality**: Full-text search across internship descriptions
3. **Application Deadline Enforcement**: Automatically close internships after closing date
4. **Analytics Dashboard**: Statistics on application success rates, popular companies
5. **Email Integration**: Automated emails for important status changes
6. **Audit Logging**: Track all system changes for accountability
7. **Data Export**: Generate CSV reports from system data
8. **Password Strength Enforcement**: Require complex passwords
9. **Session Timeout**: Auto-logout after inactivity period
10. **Undo Functionality**: Revert recent actions (would require event sourcing)

These enhancements could be added through new manager classes or extending existing ones without major architectural changes, demonstrating the flexibility of the current design.
