package com.wedding.planner.controller;

import com.wedding.planner.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

@Controller
public class RegistrationController {

    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String processRegistration(User user) {
        // Save to text file database
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("users.txt", true))) {
            writer.write(user.getEmail() + "," + user.getRole() + "," + user.getStyle());
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "redirect:/login?success";
    }
}