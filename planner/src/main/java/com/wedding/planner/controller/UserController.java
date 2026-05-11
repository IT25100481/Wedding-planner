package com.wedding.planner.controller;

import com.wedding.planner.model.User;
import com.wedding.planner.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

@Controller
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) { this.userService = userService; }

    @GetMapping("/")
    public String index() { return "index"; }

    @GetMapping("/login")
    public String login() { return "admin/login"; }

    @PostMapping("/login")
    public String doLogin(@RequestParam String email, @RequestParam String password, HttpSession session) {
        if (userService.authenticate(email, password)) {
            User user = userService.findByEmail(email);
            session.setAttribute("loggedInUser", email);
            session.setAttribute("userName", user.getFullName());
            session.setAttribute("userRole", user.getRole());
            return "redirect:/?loginSuccess=true";
        }
        return "redirect:/login?error";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String doRegister(@ModelAttribute User user, HttpSession session) {
        userService.saveUser(user);
        session.setAttribute("loggedInUser", user.getEmail());
        session.setAttribute("userName", user.getFullName());
        session.setAttribute("userRole", user.getRole());
        return "redirect:/?welcome=true";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login?logout";
    }
}