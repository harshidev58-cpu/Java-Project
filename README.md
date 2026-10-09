# SmartLearn

SmartLearn is a small Spring Boot learning management system demo for a university course. It uses Java, Maven, Spring MVC, Thymeleaf, Spring Data JPA, and a file-backed H2 database. Its sample student is enrolled in Computer Science, Calculus, General Physics, and Academic Writing. It includes a student dashboard, assignment tracker, enrollment-targeted announcements, a quiz with browser-side answer recovery, and instructor quiz reports with Excel export.

## LMS problems this project aims to address

These are the pain points SmartLearn is designed to demonstrate solutions for; they are project goals, not claims about every university platform.

| LMS pain point | SmartLearn feature |
| --- | --- |
| Course information and deadlines can be spread across different pages. | The student dashboard puts enrolled courses, progress, assignments, and announcements together. |
| Students can miss upcoming or overdue work. | The Assignment Tracker lists deadlines across enrolled sections, and the dashboard separates upcoming and overdue work. |
| Students may not receive a timely deadline reminder. | `AssignmentReminderService` checks for assignments due in 24 hours; the email/push sender is currently a stub. |
| Announcements may not apply to every student. | Announcement queries filter by the student's actual course, section, and semester enrollment. |
| A refresh or connection loss can interrupt quiz work. | Quiz answers are saved to browser `localStorage` and periodically synchronized to the server when a connection is available. |
| Students may not know why a quiz answer was wrong. | Quiz submission returns per-question correctness and explanations. |
| Instructors may need a quick view of quiz outcomes. | Instructor results show average, highest and lowest scores, students below 40%, and the hardest question. |
| Compiling class results into a spreadsheet takes manual work. | The instructor can download student results and question analysis as an `.xlsx` workbook. |

## Architecture

- `controller`: handles HTTP routes and prepares Thymeleaf views or file downloads.
- `service`: contains quiz grading, assignment and announcement behavior, analytics, and Excel generation.
- `repository`: Spring Data JPA interfaces for database access and JPQL queries.
- `model`: domain classes, enums, interfaces, relationships, and report data.
- `src/main/resources/templates`: server-rendered Thymeleaf pages and reusable fragments.

The Spring Boot application seeds a sample student, coursework, announcements, and quiz on first startup. Development data is stored in the file-backed H2 database configured in `src/main/resources/application.properties`.

## Run the application

From the project directory, run:

```bash
mvn spring-boot:run
```

Then open these URLs. The sample database usually assigns ID `1` to the first student and quiz; check the `STUDENTS` and `QUIZZES` tables in H2 if an existing database has different IDs.

- Student dashboard: <http://localhost:8080/dashboard?studentId=1>
- Sample quiz: <http://localhost:8080/quiz/1?studentId=1>
- Instructor results: <http://localhost:8080/instructor/quiz/1/results>
- Instructor Excel export: <http://localhost:8080/instructor/quiz/1/export>
- H2 console: <http://localhost:8080/h2-console>

For the H2 console, use JDBC URL `jdbc:h2:file:./data/smartlearn`, username `sa`, and the blank password configured by default. The H2 console is enabled for local development.

## Limitations and roadmap

- **No authentication yet.** The `studentId` query parameter is a demo placeholder; it is not an identity or access-control mechanism.
- **No full PWA or service worker.** Quiz answers use `localStorage` and browser online events, but the app does not provide installability, a service worker, or complete offline navigation.
- **No adaptive AI.** The quiz does not personalize questions based on a learner's performance.
- **No spaced-repetition queue.** There is no review scheduler that brings older material back at planned intervals.
- **Reminder delivery is stubbed.** The scheduled service finds the 24-hour reminder window, but email or push delivery is not connected.
- **H2 is for development.** Authentication, database migrations, and production database configuration would be needed before deployment.
