package com.example.demo.controllers;

import java.util.List;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.demo.models.Subject;
import com.example.demo.repository.SubjectRepository;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/subjects")
public class SubjectRESTController {

    @Autowired
    private SubjectRepository subjectRepository;

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
}
