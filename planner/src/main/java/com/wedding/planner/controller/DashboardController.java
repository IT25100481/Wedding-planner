package com.wedding.planner.controller;

import com.wedding.planner.model.User;
import com.wedding.planner.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpSession;

@Controller
public class DashboardController {

    private final UserService userService;

    public DashboardController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/dashboard")
    public String showDashboard(HttpSession session, Model model) {
        if (session.getAttribute("loggedInUser") == null) {
            return "redirect:/login";
        }

        // Syncing with session attributes
        model.addAttribute("role", session.getAttribute("userRole"));
        model.addAttribute("name", session.getAttribute("userName"));
        model.addAttribute("navName", session.getAttribute("navName"));
        model.addAttribute("initials", session.getAttribute("userInitials"));

        return "dashboard";
    }

    @GetMapping("/profile")
    public String showProfile(HttpSession session, Model model) {
        if (session.getAttribute("loggedInUser") == null) {
            return "redirect:/login";
        }

        // Passing all required profile details to the view
        model.addAttribute("username", session.getAttribute("loggedInUser"));
        model.addAttribute("fullName", session.getAttribute("userName"));
        model.addAttribute("email", session.getAttribute("userEmail"));
        model.addAttribute("role", session.getAttribute("userRole"));
        model.addAttribute("initials", session.getAttribute("userInitials"));

        return "profile";
    }

    /* ── PROFILE UPDATE LOGIC ── */

    @PostMapping("/update-profile")
    public String updateProfile(@RequestParam String fullName, HttpSession session) {
        String username = (String) session.getAttribute("loggedInUser");

        // 1. Update the permanent data source
        userService.updateFullName(username, fullName);

        // 2. Update session attributes immediately for the UI
        String trimmedName = fullName.trim();
        session.setAttribute("userName", trimmedName);

        String firstName = trimmedName.split("\\s+")[0];
        session.setAttribute("navName", firstName);

        // 3. Re-calculate Initials for the dynamic avatar
        String initials = firstName.substring(0, 1).toUpperCase();
        String[] parts = trimmedName.split("\\s+");
        if (parts.length > 1) {
            initials += parts[parts.length - 1].substring(0, 1).toUpperCase();
        }
        session.setAttribute("userInitials", initials);

        return "redirect:/profile?updated=true";
    }

    /* ── PASSWORD CHANGE LOGIC ── */

    @PostMapping("/change-password")
    public String changePassword(@RequestParam String currentPassword,
                                 @RequestParam String newPassword,
                                 HttpSession session) {
        String username = (String) session.getAttribute("loggedInUser");
        User user = userService.findByUsername(username);

        // Security Check: Current password must match before allowing a change
        if (user != null && user.getPassword().equals(currentPassword)) {
            userService.updatePasswordByUsername(username, newPassword);
            return "redirect:/profile?passSuccess=true";
        }

        return "redirect:/profile?passError=true";
    }

    /* ── AVATAR UPLOAD LOGIC ── */

    @PostMapping("/upload-avatar")
    public String uploadAvatar(@RequestParam("avatar") MultipartFile file) {
        if (file.isEmpty()) {
            return "redirect:/profile?uploadError=true";
        }

        // Placeholder for future file system logic
        // Path: src/main/resources/static/uploads/

        return "redirect:/profile?uploadSuccess=true";
    }
}