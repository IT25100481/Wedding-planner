package com.wedding.planner.controller;

import com.wedding.planner.model.AdminUser;
import com.wedding.planner.model.User;
import com.wedding.planner.service.AdminService;
import com.wedding.planner.service.UserService;
import com.wedding.planner.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession;
import java.util.UUID;

@Controller
public class UserController {
    private final UserService userService;
    private final AdminService adminService;

    @Autowired
    private EmailService emailService;

    public UserController(UserService userService, AdminService adminService) {
        this.userService = userService;
        this.adminService = adminService;
    }

    @GetMapping("/")
    public String index() { return "index"; }

    @GetMapping("/login")
    public String login() { return "login"; }

    @PostMapping("/login")
    public String doLogin(@RequestParam String username, @RequestParam String password, HttpSession session) {

        // ── CHECK ADMIN FIRST ──
        if (adminService.authenticate(username, password)) {
            AdminUser admin = adminService.getAdminByUsername(username);
            session.setAttribute("admin", admin);
            return "redirect:/admin/dashboard";
        }

        // ── CHECK REGULAR USER ──
        User user = userService.findByUsername(username);

        if (user == null) {
            return "redirect:/login?notFound=true";
        }

        if (user.getPassword().equals(password)) {
            String fullName = user.getFullName().trim();
            String firstName = fullName.split("\\s+")[0];

            session.setAttribute("userInitials", getInitials(fullName));
            session.setAttribute("loggedInUser", user.getUsername());
            session.setAttribute("userName", fullName);
            session.setAttribute("userEmail", user.getEmail());
            session.setAttribute("navName", firstName);
            session.setAttribute("userRole", user.getRole());

            return "redirect:/?loginSuccess=true";
        } else {
            return "redirect:/login?error=true";
        }
    }

    /* ── SECURE PROFILE SELF-DELETION WITH PASSWORD VERIFICATION ── */
    @PostMapping("/profile/delete")
    public String selfDeleteAccount(@RequestParam String verifyPassword,
                                    HttpSession session,
                                    RedirectAttributes redirectAttributes) {
        String currentLoggedInUser = (String) session.getAttribute("loggedInUser");

        if (currentLoggedInUser != null) {
            // Fetch the complete user object from the database archive
            User user = userService.findByUsername(currentLoggedInUser);

            // Check if the input password matches the stored SHA-256 hash
            if (user != null && userService.checkPassword(verifyPassword, user.getPassword())) {
                // Delete the account entry from our users.txt database tracking layer
                userService.deleteByUsername(currentLoggedInUser);

                // Invalidate the session
                session.invalidate();

                redirectAttributes.addFlashAttribute("message", "Your profile archive has been permanently erased.");
                return "redirect:/login?accountDeleted=true";
            } else {
                // Redirect back with an explicit error flag if password validation fails
                return "redirect:/profile?deleteError=true";
            }
        }

        return "redirect:/login";
    }

    /* ── FORGOT PASSWORD FLOW ── */

    @GetMapping("/forgot-password")
    public String showForgotPasswordPage() {
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam String username, @RequestParam String email) {
        User user = userService.findByUsername(username);

        if (user != null && user.getEmail().equalsIgnoreCase(email)) {
            String token = UUID.randomUUID().toString();
            String resetLink = "http://localhost:8080/reset-password?token=" + token + "&email=" + email;
            emailService.sendResetLink(email, resetLink);
            return "redirect:/login?sent=true";
        }

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
        if (userService.isUsernameTaken(user.getUsername())) {
            return "redirect:/register?usernameTaken=true";
        }

        userService.saveUser(user);

        String fullName = user.getFullName().trim();
        String firstName = fullName.split("\\s+")[0];

        session.setAttribute("userInitials", getInitials(fullName));
        session.setAttribute("loggedInUser", user.getUsername());
        session.setAttribute("userName", fullName);
        session.setAttribute("userEmail", user.getEmail());
        session.setAttribute("navName", firstName);
        session.setAttribute("userRole", user.getRole());

        return "redirect:/?welcome=true";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login?logout";
    }

    private String getInitials(String fullName) {
        String[] parts = fullName.trim().split("\\s+");
        if (parts.length == 0) return "??";
        String initials = parts[0].substring(0, 1).toUpperCase();
        if (parts.length > 1) {
            initials += parts[parts.length - 1].substring(0, 1).toUpperCase();
        }
        return initials;
    }
}