package com.wedding.planner.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


//Redirects users after login based on role.
@Controller
public class VendorLoginRedirectController {

    @GetMapping("/auth/success-redirect")
    public String determineDashboardByRole(HttpSession session) {  //HttpSession stores temporary user session data
        String role = (String) session.getAttribute("userRole");

        if ("Vendor".equalsIgnoreCase(role)) {  //role check
            return "redirect:/vendor/dashboard";
        }

        return "redirect:/?loginSuccess=true";
    }
}