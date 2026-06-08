package com.example.demo.controllers;

import java.util.List;
import java.util.HashSet;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.demo.models.Role;
import com.example.demo.models.RoleName;
import com.example.demo.models.User;
import com.example.demo.models.Grade;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.GradeRepository;
import com.example.demo.repository.SubjectRepository;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/users")
public class UserManagementController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private GradeRepository gradeRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @GetMapping
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }

    @PutMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ResponseEntity<?> updateUserRole(@PathVariable("id") Long id, @RequestParam("role") String roleName) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }

        // Prevent modifying ADMIN users
        boolean isAdmin = user.getRoles().stream()
                .anyMatch(r -> r.getName() == RoleName.ROLE_ADMIN);
        if (isAdmin) {
            return new ResponseEntity<>("Cannot modify Admin roles", HttpStatus.BAD_REQUEST);
        }

        boolean isCurrentStudent = user.getRoles().stream()
                .anyMatch(r -> r.getName() == RoleName.ROLE_STUDENT);

        Set<Role> roles = new HashSet<>();
        if ("teacher".equalsIgnoreCase(roleName)) {
            // Cascade delete all grades for this student upon promotion to Teacher
            if (isCurrentStudent) {
                List<Grade> studentGrades = gradeRepository.findByStudentId(id);
                gradeRepository.deleteAll(studentGrades);

                // Remove user from all subjects they are enrolled in
                List<com.example.demo.models.Subject> subjects = subjectRepository.findAll();
                for (com.example.demo.models.Subject subject : subjects) {
                    if (subject.getEnrolledUsers().contains(user)) {
                        subject.getEnrolledUsers().remove(user);
                        subjectRepository.save(subject);
                    }
                }
            }
            Role teacherRole = roleRepository.findByName(RoleName.ROLE_TEACHER)
                    .orElseThrow(() -> new RuntimeException("Teacher Role not found."));
            roles.add(teacherRole);
        } else if ("student".equalsIgnoreCase(roleName)) {
            Role studentRole = roleRepository.findByName(RoleName.ROLE_STUDENT)
                    .orElseThrow(() -> new RuntimeException("Student Role not found."));
            roles.add(studentRole);
        } else {
            return new ResponseEntity<>("Invalid role name. Must be 'student' or 'teacher'.", HttpStatus.BAD_REQUEST);
        }

        user.setRoles(roles);
        userRepository.save(user);
        return ResponseEntity.ok(user);
    }

    @org.springframework.web.bind.annotation.DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ResponseEntity<?> deleteUser(@PathVariable("id") Long id) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }

        boolean isAdmin = user.getRoles().stream()
                .anyMatch(r -> r.getName() == RoleName.ROLE_ADMIN);
        if (isAdmin) {
            return new ResponseEntity<>("Cannot delete Admin users", HttpStatus.BAD_REQUEST);
        }

        // Delete grades for this student
        List<Grade> studentGrades = gradeRepository.findByStudentId(id);
        gradeRepository.deleteAll(studentGrades);

        // Remove user from all subjects they are enrolled in
        List<com.example.demo.models.Subject> subjects = subjectRepository.findAll();
        for (com.example.demo.models.Subject subject : subjects) {
            if (subject.getEnrolledUsers().contains(user)) {
                subject.getEnrolledUsers().remove(user);
                subjectRepository.save(subject);
            }
        }

        userRepository.delete(user);
        return ResponseEntity.noContent().build();
    }
}
