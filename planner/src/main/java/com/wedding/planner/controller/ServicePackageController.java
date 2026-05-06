package com.wedding.planner.controller;

import com.wedding.planner.model.ServicePackage;
import com.wedding.planner.service.ServicePackageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

// Validate that the package price is not null before saving


@Controller
@RequestMapping("/packages")
public class ServicePackageController {

    @Autowired
    private ServicePackageService packageService;

    // ── READ - Show all packages ──
    @GetMapping
    public String listPackages(@RequestParam(required = false, defaultValue = "ALL") String type,
                               Model model) {
        model.addAttribute("packages", packageService.getPackagesByType(type));
        model.addAttribute("selectedType", type);
        model.addAttribute("newPackage", new ServicePackage());
        return "packages";
    }

    // ── CREATE - Add new package ──
    @PostMapping("/add")
    public String addPackage(@ModelAttribute ServicePackage pkg) {
        pkg.setActive(true);
        packageService.addPackage(pkg);
        return "redirect:/packages?success=added";
    }
    // Redirecting to the dashboard after a successful update
    // ── READ - Show edit form ──
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable String id, Model model) {
        ServicePackage pkg = packageService.getPackageById(id);
        if (pkg == null) return "redirect:/packages";
        model.addAttribute("package", pkg);
        return "package-edit";
    }

    // ── UPDATE - Save edited package ──
    @PostMapping("/update")
    public String updatePackage(@ModelAttribute ServicePackage pkg) {
        packageService.updatePackage(pkg);
        return "redirect:/packages?success=updated";
    }

    // ── DELETE - Remove package ──
    @GetMapping("/delete/{id}")
    public String deletePackage(@PathVariable String id) {
        packageService.deletePackage(id);
        return "redirect:/packages?success=deleted";
    }
}