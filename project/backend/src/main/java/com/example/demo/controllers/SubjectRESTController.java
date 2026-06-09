package com.example.demo.controllers;

import java.util.List;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.demo.models.Subject;
import com.example.demo.models.Grade;
import com.example.demo.models.User;
import com.example.demo.repository.SubjectRepository;
import com.example.demo.repository.GradeRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.example.demo.security.services.UserPrinciple;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/subjects")
public class SubjectRESTController {

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private GradeRepository gradeRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER', 'ADMIN')")
    public List<Subject> getAllSubjects() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserPrinciple userDetails = (UserPrinciple) auth.getPrincipal();
        boolean isTeacherOrAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_TEACHER") || a.getAuthority().equals("ROLE_ADMIN"));

        List<Subject> allSubjects = subjectRepository.findAll();

        if (isTeacherOrAdmin) {
            return allSubjects;
        } else {
            // Student: filter only enrolled subjects
            return allSubjects.stream()
                    .filter(s -> s.getEnrolledUsers().stream().anyMatch(u -> u.getId().equals(userDetails.getId())))
                    .collect(Collectors.toList());
        }
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<?> addSubject(@Valid @RequestBody Subject subject) {
        if (subjectRepository.existsByName(subject.getName())) {
            return new ResponseEntity<>("Subject with this name already exists", HttpStatus.BAD_REQUEST);
        }
        Subject savedSubject = subjectRepository.save(subject);
        return new ResponseEntity<>(savedSubject, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<?> updateSubject(@PathVariable("id") Long id, @Valid @RequestBody Subject subjectDetails) {
        Subject subject = subjectRepository.findById(id).orElse(null);
        if (subject == null) {
            return new ResponseEntity<>("Subject not found", HttpStatus.NOT_FOUND);
        }

        // Check if name is changing and if it conflicts with an existing subject name
        if (!subject.getName().equalsIgnoreCase(subjectDetails.getName())
                && subjectRepository.existsByName(subjectDetails.getName())) {
            return new ResponseEntity<>("Subject with this name already exists", HttpStatus.BAD_REQUEST);
        }

        subject.setName(subjectDetails.getName());
        Subject updatedSubject = subjectRepository.save(subject);
        return ResponseEntity.ok(updatedSubject);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    @Transactional
    public ResponseEntity<?> deleteSubject(@PathVariable("id") Long id) {
        Subject subject = subjectRepository.findById(id).orElse(null);
        if (subject == null) {
            return new ResponseEntity<>("Subject not found", HttpStatus.NOT_FOUND);
        }

        // Programmatically cascade delete associated grades to ensure referential
        // integrity
        List<Grade> grades = gradeRepository.findBySubjectId(id);
        gradeRepository.deleteAll(grades);

        subjectRepository.delete(subject);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{subjectId}/users/{userId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    @Transactional
    public ResponseEntity<?> enrollUser(@PathVariable("subjectId") Long subjectId,
            @PathVariable("userId") Long userId) {
        Subject subject = subjectRepository.findById(subjectId).orElse(null);
        User user = userRepository.findById(userId).orElse(null);
        if (subject == null || user == null) {
            return new ResponseEntity<>("Subject or User not found", HttpStatus.NOT_FOUND);
        }

        subject.getEnrolledUsers().add(user);
        subjectRepository.save(subject);
        return ResponseEntity.ok(subject);
    }

    @DeleteMapping("/{subjectId}/users/{userId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    @Transactional
    public ResponseEntity<?> unenrollUser(@PathVariable("subjectId") Long subjectId,
            @PathVariable("userId") Long userId) {
        Subject subject = subjectRepository.findById(subjectId).orElse(null);
        User user = userRepository.findById(userId).orElse(null);
        if (subject == null || user == null) {
            return new ResponseEntity<>("Subject or User not found", HttpStatus.NOT_FOUND);
        }

        subject.getEnrolledUsers().remove(user);
        subjectRepository.save(subject);

        // Delete grades for this user in this subject
        List<Grade> grades = gradeRepository.findBySubjectId(subjectId).stream()
                .filter(g -> g.getStudent().getId().equals(userId))
                .collect(Collectors.toList());
        gradeRepository.deleteAll(grades);

        return ResponseEntity.ok(subject);
    }
}
