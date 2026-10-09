package com.smartlearn;

import com.smartlearn.model.Announcement;
import com.smartlearn.model.AnnouncementPriority;
import com.smartlearn.model.Assignment;
import com.smartlearn.model.AssignmentStatus;
import com.smartlearn.model.Course;
import com.smartlearn.model.Enrollment;
import com.smartlearn.model.Quiz;
import com.smartlearn.model.Question;
import com.smartlearn.model.Section;
import com.smartlearn.model.Student;
import com.smartlearn.repository.AnnouncementRepository;
import com.smartlearn.repository.AssignmentRepository;
import com.smartlearn.repository.CourseRepository;
import com.smartlearn.repository.EnrollmentRepository;
import com.smartlearn.repository.QuestionRepository;
import com.smartlearn.repository.QuizRepository;
import com.smartlearn.repository.SectionRepository;
import com.smartlearn.repository.StudentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Adds sample coursework and a five-question quiz when the application starts for the first time.
 * This class demonstrates a Spring startup hook that seeds related rows through database repositories.
 */
@Component
public class DemoDataLoader implements CommandLineRunner {
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final SectionRepository sectionRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final AssignmentRepository assignmentRepository;
    private final AnnouncementRepository announcementRepository;
    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;

    public DemoDataLoader(StudentRepository studentRepository,
                          CourseRepository courseRepository,
                          SectionRepository sectionRepository,
                          EnrollmentRepository enrollmentRepository,
                          AssignmentRepository assignmentRepository,
                          AnnouncementRepository announcementRepository,
                          QuizRepository quizRepository,
                          QuestionRepository questionRepository) {
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
        this.sectionRepository = sectionRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.assignmentRepository = assignmentRepository;
        this.announcementRepository = announcementRepository;
        this.quizRepository = quizRepository;
        this.questionRepository = questionRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        Student student = studentRepository.findByEmail("alex@example.edu").orElse(null);
        if (student == null) {
            student = seedCoursework();
        }

        seedAdditionalSubjects(student);
        seedQuizIfMissing();
    }

    private Student seedCoursework() {
        Course course = courseRepository.save(new Course(null, "CS101", "Introduction to Computer Science",
                "Core programming and problem-solving concepts."));
        Section section = sectionRepository.save(new Section(null, "Section A", course, "Fall 2026",
                "Weekly topics include programming basics, data types, and problem solving.",
                List.of("Week 1: Course notes", "Java setup guide", "Problem-solving worksheet")));
        Student student = studentRepository.save(new Student(null, "Alex Student", "alex@example.edu"));

        enrollmentRepository.save(new Enrollment(null, student, section, LocalDate.now(), 35));

        LocalDateTime now = LocalDateTime.now();
        assignmentRepository.save(new Assignment(null, "Variables practice", "Complete the short practice set.",
                now.plusDays(3), AssignmentStatus.PENDING, section));
        assignmentRepository.save(new Assignment(null, "First programming exercise", "Submit a small Java program.",
                now.minusDays(1), AssignmentStatus.PENDING, section));
        assignmentRepository.save(new Assignment(null, "Reading response", "Respond to the assigned reading.",
                now.plusDays(5), AssignmentStatus.SUBMITTED, section));
        assignmentRepository.save(new Assignment(null, "Course introduction quiz", "Review your graded quiz.",
                now.minusDays(3), AssignmentStatus.GRADED, section));

        announcementRepository.save(new Announcement(null, "Room update", "This week's lab meets in Room 204.",
                course, section, section.getSemester(), AnnouncementPriority.HIGH, now.minusHours(1)));
        announcementRepository.save(new Announcement(null, "Welcome to CS101", "Please review the syllabus before class.",
                course, null, section.getSemester(), AnnouncementPriority.MEDIUM, now.minusDays(1)));
        return student;
    }

    /** Adds extra demo courses only when their course codes are not already stored. */
    private void seedAdditionalSubjects(Student student) {
        seedSubjectIfMissing(student, "MATH201", "Calculus I",
                "Limits, derivatives, and introductory integration.", 20,
                "Calculus practice", "Practice derivative rules before the next tutorial.", AnnouncementPriority.MEDIUM);
        seedSubjectIfMissing(student, "PHYS110", "General Physics",
                "Mechanics, motion, and introductory laboratory work.", 15,
                "Motion worksheet", "Bring your motion graph to the next lab.", AnnouncementPriority.HIGH);
        seedSubjectIfMissing(student, "ENG102", "Academic Writing",
                "Clear academic arguments, research, and revision.", 45,
                "Thesis statement draft", "Upload a first thesis statement for peer review.", AnnouncementPriority.LOW);
    }

    /** Persists one course, its section, enrollment, assignment, and matching announcement. */
    private void seedSubjectIfMissing(Student student, String code, String title, String description,
                                      int progress, String assignmentTitle, String announcementMessage,
                                      AnnouncementPriority priority) {
        if (courseRepository.existsByCode(code)) {
            return;
        }

        Course course = courseRepository.save(new Course(null, code, title, description));
        Section section = sectionRepository.save(new Section(null, "Section A", course, "Fall 2026",
                title + " course outline and weekly learning materials.",
                List.of(title + " syllabus", "Week 1 learning notes", "Practice resources")));
        enrollmentRepository.save(new Enrollment(null, student, section, LocalDate.now(), progress));

        LocalDateTime now = LocalDateTime.now();
        assignmentRepository.save(new Assignment(null, assignmentTitle,
                "Complete the practice work for " + title + ".", now.plusDays(2), AssignmentStatus.PENDING, section));
        announcementRepository.save(new Announcement(null, title + " class update", announcementMessage,
                course, section, section.getSemester(), priority, now));
    }

    private void seedQuizIfMissing() {
        if (quizRepository.findByTitle("Java Foundations Quiz").isPresent()) {
            return;
        }

        Quiz quiz = quizRepository.save(new Quiz(null, "Java Foundations Quiz",
                "A short five-question check of Java basics.", 45));
        questionRepository.saveAll(List.of(
                new Question(null, quiz, 1, "Which file extension is used for Java source files?",
                        List.of(".java", ".class", ".js", ".py"), ".java",
                        "Java source code is normally saved in files ending with .java. A .class file contains compiled bytecode."),
                new Question(null, quiz, 2, "Which keyword begins a class declaration in Java?",
                        List.of("class", "function", "struct", "module"), "class",
                        "The class keyword declares a class, which is a blueprint for objects."),
                new Question(null, quiz, 3, "Which Java type stores a whole number?",
                        List.of("int", "boolean", "String", "char"), "int",
                        "The primitive int type stores whole-number values. String stores text and boolean stores true or false."),
                new Question(null, quiz, 4, "Which method is the usual Java application entry point?",
                        List.of("public static void main(String[] args)", "start()", "runProgram()", "constructor()"),
                        "public static void main(String[] args)",
                        "The Java runtime looks for the public static main method to begin a standard application."),
                new Question(null, quiz, 5, "What does == compare for primitive values?",
                        List.of("Whether the values are equal", "Whether the variables have the same name",
                                "Whether both values are text", "Whether either value is null"),
                        "Whether the values are equal",
                        "For primitive values, == compares the values. For objects, it compares references; use equals() for value equality when appropriate.")
        ));
    }
}
