package com.wedding.planner.controller;

import com.wedding.planner.model.User;
import com.wedding.planner.service.UserService;
import com.wedding.planner.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import java.util.UUID;

@Controller
public class UserController {
    private final UserService userService;

    @Autowired
    private EmailService emailService;

    public UserController(UserService userService) { this.userService = userService; }

    @GetMapping("/")
    public String index() { return "index"; }

    @GetMapping("/login")
    public String login() { return "login"; }

    @PostMapping("/login")
    public String doLogin(@RequestParam String email, @RequestParam String password, HttpSession session) {
        User user = userService.findByEmail(email);
        if (user == null) {
            return "redirect:/register?notFound=true";
        }

        if (user.getPassword().equals(password)) {
            String firstName = user.getFullName().trim().split("\\s+")[0];
            session.setAttribute("loggedInUser", email);
            session.setAttribute("userName", user.getFullName());
            session.setAttribute("navName", firstName);
            session.setAttribute("userRole", user.getRole());
            return "redirect:/?loginSuccess=true";
        } else {
            return "redirect:/login?error=true";
        }
    }

    /* ── FORGOT PASSWORD FLOW ── */

    @GetMapping("/forgot-password")
    public String showForgotPasswordPage() {
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam String email) {
        User user = userService.findByEmail(email);
        if (user != null) {
            String token = UUID.randomUUID().toString();
            String resetLink = "http://localhost:8080/reset-password?token=" + token + "&email=" + email;
            emailService.sendResetLink(email, resetLink);
        }
        return "redirect:/login?sent=true";
    }

    @GetMapping("/reset-password")
    public String showResetPage(@RequestParam String token, @RequestParam String email, Model model) {
        model.addAttribute("token", token);
        model.addAttribute("email", email);
        return "reset-password";
    }

    // NEW: The method that actually saves the new password
    @PostMapping("/reset-password")
    public String handlePasswordReset(@RequestParam String email, @RequestParam String newPassword) {
        User user = userService.findByEmail(email);
        if (user != null) {
            // Update the user object with the new password
            user.setPassword(newPassword);
            // Save the user (this will update the line in users.txt)
            userService.saveUser(user);
        }
        return "redirect:/login?resetSuccess=true";
    }

    /* ────────────────────────── */

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String doRegister(@ModelAttribute User user, HttpSession session) {
        userService.saveUser(user);
        String firstName = user.getFullName().trim().split("\\s+")[0];
        session.setAttribute("loggedInUser", user.getEmail());
        session.setAttribute("userName", user.getFullName());
        session.setAttribute("navName", firstName);
        session.setAttribute("userRole", user.getRole());
        return "redirect:/?welcome=true";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login?logout";
    }
}