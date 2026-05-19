package com.wedding.planner.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class VendorLoginRedirectController {

    @GetMapping("/auth/success-redirect")
    public String determineDashboardByRole(HttpSession session) {
        String role = (String) session.getAttribute("userRole");

        if ("Vendor".equalsIgnoreCase(role)) {
            return "redirect:/vendor/dashboard";
        }

        return "redirect:/?loginSuccess=true";
    }
}