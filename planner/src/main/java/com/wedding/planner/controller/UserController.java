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

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public String index() { return "index"; }

    @GetMapping("/login")
    public String login() { return "login"; }

    @PostMapping("/login")
    public String doLogin(@RequestParam String username, @RequestParam String password, HttpSession session) {
        // CHANGED: Search by Username instead of Email
        User user = userService.findByUsername(username);

        if (user == null) {
            return "redirect:/login?notFound=true";
        }

        if (user.getPassword().equals(password)) {
            String firstName = user.getFullName().trim().split("\\s+")[0];

            // Set session attributes using Username
            session.setAttribute("loggedInUser", user.getUsername());
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
    public String processForgotPassword(@RequestParam String username, @RequestParam String email) {
        // SECURITY UPDATE: Find by Username first
        User user = userService.findByUsername(username);

        // CHECK: Does username exist AND does the registered email match the input?
        if (user != null && user.getEmail().equalsIgnoreCase(email)) {
            String token = UUID.randomUUID().toString();
            String resetLink = "http://localhost:8080/reset-password?token=" + token + "&email=" + email;
            emailService.sendResetLink(email, resetLink);
            return "redirect:/login?sent=true";
        }

        // Return with error if they don't match
        return "redirect:/forgot-password?mismatch=true";
    }

    @GetMapping("/reset-password")
    public String showResetPage(@RequestParam String token, @RequestParam String email, Model model) {
        model.addAttribute("token", token);
        model.addAttribute("email", email);
        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String handlePasswordReset(@RequestParam String email, @RequestParam String newPassword) {
        User user = userService.findByEmail(email);
        if (user != null) {
            // Service method that overwrites the file line
            userService.updatePassword(email, newPassword);
        }
        return "redirect:/login?resetSuccess=true";
    }

    /* ── REGISTRATION ── */

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String doRegister(@ModelAttribute User user, HttpSession session) {
        // CHECK: Unique Username Validation
        if (userService.isUsernameTaken(user.getUsername())) {
            return "redirect:/register?usernameTaken=true";
        }

        userService.saveUser(user);

        String firstName = user.getFullName().trim().split("\\s+")[0];
        session.setAttribute("loggedInUser", user.getUsername());
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