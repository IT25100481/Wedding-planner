package com.wedding.planner.controller;

import com.wedding.planner.model.ServicePackage;
import com.wedding.planner.service.ServicePackageService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ServicePackageController {

    @Autowired
    private ServicePackageService packageService;

    // ════════════════════════════════════════════
    // CLIENT SIDE
    // /packages → client-packages.html (view only + Book Now)
    // ════════════════════════════════════════════

    @GetMapping("/packages")
    public String clientPackages(
            @RequestParam(required = false, defaultValue = "ALL") String type,
            Model model) {
        model.addAttribute("packages", packageService.getPackagesByType(type));
        model.addAttribute("selectedType", type);
        return "packages/client-packages";       // → templates/packages/client-packages.html
    }

    // ════════════════════════════════════════════
    // ADMIN SIDE
    // /admin/packages → packages.html (existing file, add + edit + delete)
    // ════════════════════════════════════════════

    private boolean isAdmin(HttpSession session) {
        return session.getAttribute("admin") != null;
    }

    // Admin views all packages + add form
    @GetMapping("/admin/packages")
    public String adminPackages(
            @RequestParam(required = false, defaultValue = "ALL") String type,
            Model model, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/admin/login";
        model.addAttribute("packages", packageService.getPackagesByType(type));
        model.addAttribute("selectedType", type);
        model.addAttribute("newPackage", new ServicePackage());
        return "packages/packages";              // → templates/packages/packages.html (existing)
    }

    // Admin adds a package
    @PostMapping("/admin/packages/add")
    public String addPackage(@ModelAttribute ServicePackage pkg,
                             HttpSession session,
                             RedirectAttributes ra) {
        if (!isAdmin(session)) return "redirect:/admin/login";
        pkg.setActive(true);
        packageService.addPackage(pkg);
        ra.addFlashAttribute("message", "Package added successfully!");
        return "redirect:/admin/packages";
    }

    // Admin edit form
    @GetMapping("/admin/packages/edit/{id}")
    public String showEditForm(@PathVariable String id,
                               Model model, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/admin/login";
        ServicePackage pkg = packageService.getPackageById(id);
        if (pkg == null) return "redirect:/admin/packages";
        model.addAttribute("package", pkg);
        return "packages/package-edit";          // → templates/packages/package-edit.html
    }

    // Admin saves edit
    @PostMapping("/admin/packages/update")
    public String updatePackage(@ModelAttribute ServicePackage pkg,
                                HttpSession session,
                                RedirectAttributes ra) {
        if (!isAdmin(session)) return "redirect:/admin/login";
        packageService.updatePackage(pkg);
        ra.addFlashAttribute("message", "Package updated successfully!");
        return "redirect:/admin/packages";
    }

    // Admin deletes package
    @GetMapping("/admin/packages/delete/{id}")
    public String deletePackage(@PathVariable String id,
                                HttpSession session,
                                RedirectAttributes ra) {
        if (!isAdmin(session)) return "redirect:/admin/login";
        packageService.deletePackage(id);
        ra.addFlashAttribute("message", "Package deleted.");
        return "redirect:/admin/packages";
    }
}