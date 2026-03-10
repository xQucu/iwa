package com.example.demo.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.example.demo.models.Quad;

@Controller
public class QuadController {

    @RequestMapping("/quad")
    public String quad(Model model) {
        model.addAttribute("message", "Simple String from QuadController.");
        Quad newQuad = new Quad();
        model.addAttribute("quad", newQuad);
        return "quad";
    }

    @RequestMapping(value = "/solveQuad.html", method = RequestMethod.POST)
    public String addStudent(@ModelAttribute("quad") Quad quad) {
        quad.handle();
        System.out.println(quad.getSolution());
        return "redirect:quad";
    }
}
