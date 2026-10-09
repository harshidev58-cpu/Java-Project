# Java concepts used in SmartLearn

| Concept | Class or file | Why it is used there |
| --- | --- | --- |
| Inheritance | `model/User.java`, `model/Student.java`, `model/Instructor.java`, `model/Admin.java` | The concrete user types reuse shared identity fields and behavior from `User`. |
| Abstraction | `model/User.java`, `model/Trackable.java`, `model/Gradable.java` | The abstract user defines shared state while contracts describe behavior without tying it to one implementation. |
| Interfaces | `model/Trackable.java`, `model/Gradable.java`, `model/Assignment.java`, `model/AssignmentSubmission.java` | Implementations provide a common contract for trackable deadlines and gradable work. |
| Enums | `model/Role.java`, `model/AssignmentStatus.java`, `model/AnnouncementPriority.java` | Enums restrict roles, assignment states, and announcement priorities to known values. |
| Generics | `repository/*.java` (`JpaRepository<Assignment, Long>`, etc.) | Parameterized repository interfaces provide type-safe persistence operations for each entity and ID type. |
| Collections | `model/QuizAttempt.java`, `model/Quiz.java`, `model/Section.java`, `service/QuizService.java` | Lists and maps hold related questions, materials, attempts, and saved answers. |
| Streams | `service/QuizAnalyticsService.java`, `service/AssignmentTrackerService.java`, `service/AnnouncementService.java`, `controller/StudentPageController.java` | Stream pipelines filter, transform, sort, and summarize collections without manually managing loop state. |
| Exceptions | `exception/InvalidQuizAttemptException.java`, `exception/EnrollmentNotFoundException.java`, `exception/GlobalExceptionHandler.java` | Custom exceptions represent expected application errors, and advice maps them to HTTP responses. |
| Dependency injection | Controllers and services with constructor parameters | Spring supplies repositories and services so classes can depend on collaborators through interfaces and constructors. |
| JPA | `model/*.java`, `repository/*.java`, `src/main/resources/application.properties` | Entity annotations and relationships map Java objects to relational rows, while Spring Data provides repository queries. |
| Scheduling | `service/AssignmentReminderService.java`, `SmartLearnApplication.java` | `@Scheduled` runs the reminder check periodically after scheduling is enabled; message delivery remains a stub. |
| JUnit and Mockito | `src/test/java/com/smartlearn/service/*Test.java` | JUnit runs focused behavior tests, while Mockito replaces repositories with controlled test doubles. |
| JUnit and `@DataJpaTest` | `src/test/java/com/smartlearn/repository/*DataJpaTest.java` | The repository tests use a real test database and JPA so they verify that JPQL ordering works after rows are persisted. |
