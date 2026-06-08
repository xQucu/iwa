package com.example.demo.controllers;

import java.util.List;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.demo.message.request.GradeRequest;
import com.example.demo.models.Grade;
import com.example.demo.models.RoleName;
import com.example.demo.models.Subject;
import com.example.demo.models.User;
import com.example.demo.repository.GradeRepository;
import com.example.demo.repository.SubjectRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.services.UserPrinciple;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/grades")
public class GradeRESTController {

    @Autowired
    private GradeRepository gradeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @GetMapping
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER', 'ADMIN')")
    public ResponseEntity<?> getGrades(
            @AuthenticationPrincipal UserPrinciple userPrinciple,
            @RequestParam(value = "studentId", required = false) Long studentId) {

        boolean isStudent = userPrinciple.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_STUDENT"));

        if (isStudent) {
            // Students can only see their own grades
            List<Grade> studentGrades = gradeRepository.findByStudentId(userPrinciple.getId());
            return ResponseEntity.ok(studentGrades);
        } else {
            // Teachers can see all grades or filter by a specific student
            if (studentId != null) {
                List<Grade> studentGrades = gradeRepository.findByStudentId(studentId);
                return ResponseEntity.ok(studentGrades);
            }
            List<Grade> allGrades = gradeRepository.findAll();
            return ResponseEntity.ok(allGrades);
        }
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<?> gradeStudent(@Valid @RequestBody GradeRequest gradeRequest) {
        // Find target student
        User student = userRepository.findById(gradeRequest.getStudentId()).orElse(null);
        if (student == null) {
            return new ResponseEntity<>("Student not found", HttpStatus.NOT_FOUND);
        }

        // Verify the user is actually a student
        boolean isStudent = student.getRoles().stream()
                .anyMatch(role -> role.getName() == RoleName.ROLE_STUDENT);
        if (!isStudent) {
            return new ResponseEntity<>("Target user is not a student", HttpStatus.BAD_REQUEST);
        }

        // Find subject
        Subject subject = subjectRepository.findById(gradeRequest.getSubjectId()).orElse(null);
        if (subject == null) {
            return new ResponseEntity<>("Subject not found", HttpStatus.NOT_FOUND);
        }

        // Verify enrollment
        boolean isEnrolled = subject.getEnrolledUsers().stream()
                .anyMatch(u -> u.getId().equals(student.getId()));
        if (!isEnrolled) {
            return new ResponseEntity<>("Student is not enrolled in this subject", HttpStatus.BAD_REQUEST);
        }

        // Create and save new grade
        Grade grade = new Grade(
                gradeRequest.getValue(),
                gradeRequest.getDescription(),
                subject,
                student
        );
        Grade savedGrade = gradeRepository.save(grade);
        return new ResponseEntity<>(savedGrade, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<?> updateGrade(@PathVariable("id") Long id, @Valid @RequestBody GradeRequest gradeRequest) {
        Grade grade = gradeRepository.findById(id).orElse(null);
        if (grade == null) {
            return new ResponseEntity<>("Grade not found", HttpStatus.NOT_FOUND);
        }

        // Verify and update student
        User student = userRepository.findById(gradeRequest.getStudentId()).orElse(null);
        if (student == null) {
            return new ResponseEntity<>("Student not found", HttpStatus.NOT_FOUND);
        }
        boolean isStudent = student.getRoles().stream()
                .anyMatch(role -> role.getName() == RoleName.ROLE_STUDENT);
        if (!isStudent) {
            return new ResponseEntity<>("Target user is not a student", HttpStatus.BAD_REQUEST);
        }

        // Verify and update subject
        Subject subject = subjectRepository.findById(gradeRequest.getSubjectId()).orElse(null);
        if (subject == null) {
            return new ResponseEntity<>("Subject not found", HttpStatus.NOT_FOUND);
        }

        // Verify enrollment
        boolean isEnrolled = subject.getEnrolledUsers().stream()
                .anyMatch(u -> u.getId().equals(student.getId()));
        if (!isEnrolled) {
            return new ResponseEntity<>("Student is not enrolled in this subject", HttpStatus.BAD_REQUEST);
        }

        // Perform the updates
        grade.setStudent(student);
        grade.setSubject(subject);
        grade.setValue(gradeRequest.getValue());
        grade.setDescription(gradeRequest.getDescription());

        Grade updatedGrade = gradeRepository.save(grade);
        return ResponseEntity.ok(updatedGrade);
    }

    @org.springframework.web.bind.annotation.DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<?> deleteGrade(@PathVariable("id") Long id) {
        Grade grade = gradeRepository.findById(id).orElse(null);
        if (grade == null) {
            return new ResponseEntity<>("Grade not found", HttpStatus.NOT_FOUND);
        }

        gradeRepository.delete(grade);
        return ResponseEntity.noContent().build();
    }
}
