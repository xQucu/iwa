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
import com.example.demo.repository.SubjectRepository;
import com.example.demo.repository.GradeRepository;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/subjects")
public class SubjectRESTController {

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private GradeRepository gradeRepository;

    @GetMapping
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER')")
    public List<Subject> getAllSubjects() {
        return subjectRepository.findAll();
    }

    @PostMapping
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<?> addSubject(@Valid @RequestBody Subject subject) {
        if (subjectRepository.existsByName(subject.getName())) {
            return new ResponseEntity<>("Subject with this name already exists", HttpStatus.BAD_REQUEST);
        }
        Subject savedSubject = subjectRepository.save(subject);
        return new ResponseEntity<>(savedSubject, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
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
    @PreAuthorize("hasRole('TEACHER')")
    @Transactional
    public ResponseEntity<?> deleteSubject(@PathVariable("id") Long id) {
        Subject subject = subjectRepository.findById(id).orElse(null);
        if (subject == null) {
            return new ResponseEntity<>("Subject not found", HttpStatus.NOT_FOUND);
        }

        // Programmatically cascade delete associated grades to ensure referential integrity
        List<Grade> grades = gradeRepository.findBySubjectId(id);
        gradeRepository.deleteAll(grades);

        subjectRepository.delete(subject);
        return ResponseEntity.noContent().build();
    }
}
