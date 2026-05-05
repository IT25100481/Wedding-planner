package com.wedding.planner.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProfileController {

    @GetMapping("/vendor/profile")
    public String showProfile(Model model) {
        // These details will appear on your vendor-profile.html page
        model.addAttribute("vendorName", "Thisumi Gajaman");
        model.addAttribute("location", "Colombo, Sri Lanka");
        model.addAttribute("ratings", 4.8);
        model.addAttribute("serviceCount", 5);
        model.addAttribute("description", "Professional wedding vendor specializing in elegant decor.");
        return "vendor-profile";
    }
}