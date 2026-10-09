package com.smartlearn.service;

import com.smartlearn.exception.ResourceNotFoundException;
import com.smartlearn.model.Student;
import com.smartlearn.repository.StudentRepository;
import org.springframework.stereotype.Service;

/**
 * Holds the application logic for student operations.
 * This class demonstrates the service layer separating business rules from HTTP and data access.
 */
@Service
public class StudentService {
    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public Student findStudent(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found: " + id));
    }

    public Student saveStudent(Student student) {
        return studentRepository.save(student);
    }
}
