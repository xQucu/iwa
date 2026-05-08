package com.example.demo.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.models.Student;
import com.example.demo.models.Address;
import com.example.demo.models.Account;
import com.example.demo.repository.AddressRepository;
import com.example.demo.repository.AccountRepository;
import com.example.demo.repository.StudentRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("students")
public class StudentRESTController {
    private StudentRepository studentRepository;
    private AddressRepository addressRepository;
    private AccountRepository accountRepository;

    @Autowired
    public StudentRESTController(StudentRepository studentRepository, AddressRepository addressRepository,
            AccountRepository accountRepository) {
        this.studentRepository = studentRepository;
        this.addressRepository = addressRepository;
        this.accountRepository = accountRepository;
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<Student> findAllStudents() {
        return studentRepository.findAll();
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Student> addStudent(@RequestBody Student student) {
        if (student.getAddress() != null) {
            Long addrId = student.getAddress().getId();
            if (addrId == null || addrId == 0) {
                addressRepository.save(student.getAddress());
            } else if (!addressRepository.existsById(addrId)) {
                addressRepository.save(student.getAddress());
            }
        }
        if (student.getAccount() != null) {
            Long accId = student.getAccount().getId();
            if (accId == null || accId == 0) {
                accountRepository.save(student.getAccount());
            } else if (!accountRepository.existsById(accId)) {
                accountRepository.save(student.getAccount());
            }
        }
        studentRepository.save(student);
        return new ResponseEntity<Student>(student, HttpStatus.CREATED);
    };

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Student> deleteStudent(@PathVariable("id") long id) {
        Student student = studentRepository.findById(id);
        if (student == null) {
            System.out.println("Student not found!");
            return new ResponseEntity<Student>(HttpStatus.NOT_FOUND);
        }

        Long addressId = student.getAddress() != null ? student.getAddress().getId() : null;
        Long accountId = student.getAccount() != null ? student.getAccount().getId() : null;

        studentRepository.deleteById(id);

        if (addressId != null && !studentRepository.existsByAddressId(addressId)) {
            addressRepository.deleteById(addressId);
        }

        if (accountId != null && !studentRepository.existsByAccountId(accountId)) {
            accountRepository.deleteById(accountId);
        }

        return new ResponseEntity<Student>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<Student> updateStudent(@RequestBody Student student, @PathVariable("id") long id) {
        if (studentRepository.existsById(id)) {
            student.setId(id);
            studentRepository.save(student);
            return new ResponseEntity<Student>(student, HttpStatus.CREATED);
        }
        studentRepository.save(student);
        return new ResponseEntity<Student>(student, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PATCH)
    public ResponseEntity<Student> updatePartOfStudent(@RequestBody Map<String, Object> updates,
            @PathVariable("id") long id) {
        Student student = studentRepository.findById(id);
        if (student == null) {
            System.out.println("Student not found");
            return new ResponseEntity<Student>(HttpStatus.NOT_FOUND);
        }
        partialUpdate(student, updates);
        return new ResponseEntity<Student>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(method = RequestMethod.PUT)
    // Substitite collection
    public void substituteCollection(@RequestBody List<Student> students) {
        deleteAllStudents();
        for (Student student : students) {
            studentRepository.save(student);
        }
        ResponseEntity.ok();
        return;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    // Get data about single student
    public ResponseEntity<Student> getStudentInfo(@PathVariable("id") long id) {
        Student student = studentRepository.findById(id);
        if (student == null) {
            System.out.println("Student not found!");
            return new ResponseEntity<Student>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Student>(student, HttpStatus.OK);
    }

    @RequestMapping(value = "/", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteAllStudents() {
        List<Long> ids = studentRepository.findAll().stream().map(Student::getId).toList();
        for (Long id : ids) {
            var studentOpt = studentRepository.findById(id);
            if (studentOpt.isPresent()) {
                Student student = studentOpt.get();
                Long addressId = student.getAddress() != null ? student.getAddress().getId() : null;
                Long accountId = student.getAccount() != null ? student.getAccount().getId() : null;

                studentRepository.deleteById(id);

                if (addressId != null && !studentRepository.existsByAddressId(addressId)) {
                    addressRepository.deleteById(addressId);
                }
                if (accountId != null && !studentRepository.existsByAccountId(accountId)) {
                    accountRepository.deleteById(accountId);
                }
            }
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    private void partialUpdate(Student student, Map<String, Object> updates) {
        if (updates.containsKey("firstName")) {
            student.setFirstName((String) updates.get("firstName"));
        }
        if (updates.containsKey("lastName")) {
            student.setLastName((String) updates.get("lastName"));
        }
        if (updates.containsKey("email")) {
            student.setEmail((String) updates.get("email"));
        }
        if (updates.containsKey("number")) {
            student.setNumber((String) updates.get("number"));
        }
        studentRepository.save(student);
    }
}
