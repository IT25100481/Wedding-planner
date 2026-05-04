package com.wedding.planner.controller;

import com.wedding.planner.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

@Controller
public class RegistrationController {

    @GetMapping("/register")
    public String showRegistration(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String handleRegistration(@ModelAttribute User user) {
        // Saving to your text-file database
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("users.txt", true))) {
            String record = String.format("%s,%s,%s",
                    user.getEmail(), user.getRole(), user.getStyle());
            writer.write(record);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "redirect:/?success=true";
    }
}