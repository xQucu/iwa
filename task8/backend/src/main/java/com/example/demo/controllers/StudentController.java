package com.example.demo.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.example.demo.models.Student;

@Controller
public class StudentController {

    @RequestMapping("/student")
    public String student(Model model) {
        model.addAttribute("message", "Simple String from StudentController.");
        Student newStudent = new Student();
        model.addAttribute("student", newStudent);
        return "student";
    }

    @RequestMapping(value = "/addStudent.html", method = RequestMethod.POST)
    public String addStudent(@ModelAttribute("student") Student student) {
        System.out.println(student.getFirstName() + " " + student.getLastName() + " " + student.getEmail() + " "
                + student.getNumber());
        return "redirect:student";
    }
}
