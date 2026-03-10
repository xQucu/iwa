package com.example.demo.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.example.demo.models.Fib;

@Controller
public class FibController {

    @RequestMapping("/fib")
    public String quad(Model model) {
        model.addAttribute("message", "Simple String from FibController.");
        Fib newFib = new Fib();
        model.addAttribute("fib", newFib);
        return "fib";
    }

    @RequestMapping(value = "/solveFib.html", method = RequestMethod.POST)
    public String addStudent(@ModelAttribute("fib") Fib fib) {
        fib.handle();
        System.out.println(fib.getSolution());
        return "redirect:fib";
    }
}
