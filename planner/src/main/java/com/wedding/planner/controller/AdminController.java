package com.wedding.planner.controller;

import com.wedding.planner.model.AdminUser;
import com.wedding.planner.service.AdminService;
import com.wedding.planner.service.UserService;
import com.wedding.planner.service.VendorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;
    private final VendorService vendorService;

    @Autowired
    private UserService userService;

    @Autowired
    public AdminController(AdminService adminService, VendorService vendorService) {
        this.adminService = adminService;
        this.vendorService = vendorService;
    }

    // ── LOGIN ──
    @GetMapping("/login")
    public String loginPage() {
        return "admin/login";
    }

    @PostMapping("/login")
    public String processLogin(@RequestParam String username,
                               @RequestParam String password,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        if (adminService.authenticate(username, password)) {
            AdminUser admin = adminService.getAdminByUsername(username);
            session.setAttribute("admin", admin);
            return "redirect:/admin/dashboard";
        } else {
            redirectAttributes.addFlashAttribute("error", "Invalid credentials");
            return "redirect:/admin/login";
        }
    }

    // ── LOGOUT ──
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/admin/login";
    }

    // ── DASHBOARD ──
    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session) {
        if (session.getAttribute("admin") == null) return "redirect:/admin/login";
        model.addAttribute("totalVendors", vendorService.getTotalCount());
        model.addAttribute("pendingApprovals", vendorService.getPendingCount());
        model.addAttribute("totalAdmins", adminService.getAllAdmins().size());
        model.addAttribute("recentLogins", adminService.getRecentLogins());
        model.addAttribute("registeredClients", userService.getAllUsersPublic());
        return "admin/dashboard";
    }

    // ════════════════════════════════════════════
    // VENDOR MANAGEMENT
    // ════════════════════════════════════════════

    @GetMapping("/vendors")
    public String listVendors(Model model, HttpSession session) {
        if (session.getAttribute("admin") == null) return "redirect:/admin/login";
        model.addAttribute("vendors", vendorService.getAllVendors());
        return "admin/vendor-list";
    }

    @PostMapping("/vendors/approve/{id}")
    public String approveVendor(@PathVariable String id,
                                RedirectAttributes ra,
                                HttpSession session) {
        if (session.getAttribute("admin") == null) return "redirect:/admin/login";
        vendorService.updateStatus(id, "APPROVED");
        ra.addFlashAttribute("message", "Vendor approved successfully!");
        return "redirect:/admin/vendors";
    }

    @PostMapping("/vendors/reject/{id}")
    public String rejectVendor(@PathVariable String id,
                               RedirectAttributes ra,
                               HttpSession session) {
        if (session.getAttribute("admin") == null) return "redirect:/admin/login";
        vendorService.updateStatus(id, "REJECTED");
        ra.addFlashAttribute("message", "Vendor rejected.");
        return "redirect:/admin/vendors";
    }

    @PostMapping("/vendors/add")
    public String addVendor(@ModelAttribute com.wedding.planner.model.Service vendor,
                            RedirectAttributes ra,
                            HttpSession session) {
        if (session.getAttribute("admin") == null) return "redirect:/admin/login";
        vendorService.addVendor(vendor);
        ra.addFlashAttribute("message", "Vendor added successfully!");
        return "redirect:/admin/vendors";
    }

    @GetMapping("/vendors/edit/{id}")
    public String editVendorForm(@PathVariable String id,
                                 Model model,
                                 HttpSession session) {
        if (session.getAttribute("admin") == null) return "redirect:/admin/login";
        com.wedding.planner.model.Service vendor = vendorService.getVendorById(id);
        if (vendor == null) return "redirect:/admin/vendors";
        model.addAttribute("vendor", vendor);
        return "admin/vendor-edit";
    }

    @PostMapping("/vendors/update")
    public String updateVendor(@ModelAttribute com.wedding.planner.model.Service vendor,
                               RedirectAttributes ra,
                               HttpSession session) {
        if (session.getAttribute("admin") == null) return "redirect:/admin/login";
        vendorService.updateVendor(vendor);
        ra.addFlashAttribute("message", "Vendor updated successfully!");
        return "redirect:/admin/vendors";
    }

    @GetMapping("/vendors/delete/{id}")
    public String deleteVendor(@PathVariable String id,
                               RedirectAttributes ra,
                               HttpSession session) {
        if (session.getAttribute("admin") == null) return "redirect:/admin/login";
        vendorService.deleteVendor(id);
        ra.addFlashAttribute("message", "Vendor deleted successfully!");
        return "redirect:/admin/vendors";
    }

    // ════════════════════════════════════════════
    // CLIENT MANAGEMENT
    // ════════════════════════════════════════════

    @GetMapping("/clients/delete/{username}")
    public String deleteClient(@PathVariable String username,
                               RedirectAttributes ra,
                               HttpSession session) {
        if (session.getAttribute("admin") == null) return "redirect:/admin/login";
        userService.deleteByUsername(username);
        ra.addFlashAttribute("message", "Client deleted successfully.");
        return "redirect:/admin/dashboard";
    }

    // ════════════════════════════════════════════
    // ADMIN CRUD
    // ════════════════════════════════════════════

    @GetMapping("/admins")
    public String listAdmins(Model model, HttpSession session) {
        if (session.getAttribute("admin") == null) return "redirect:/admin/login";
        model.addAttribute("admins", adminService.getAllAdmins());
        return "admin/admins";
    }

    @GetMapping("/admins/add")
    public String addAdminForm(Model model, HttpSession session) {
        if (session.getAttribute("admin") == null) return "redirect:/admin/login";
        model.addAttribute("admin", new AdminUser());
        return "admin/admin-form";
    }

    @GetMapping("/admins/edit/{id}")
    public String editAdminForm(@PathVariable String id,
                                Model model, HttpSession session) {
        if (session.getAttribute("admin") == null) return "redirect:/admin/login";
        AdminUser admin = adminService.getAllAdmins().stream()
                .filter(a -> a.getId().equals(id))
                .findFirst().orElse(null);
        if (admin == null) return "redirect:/admin/admins";
        model.addAttribute("admin", admin);
        return "admin/admin-form";
    }

    @PostMapping("/admins/save")
    public String saveAdmin(@ModelAttribute AdminUser admin,
                            RedirectAttributes redirectAttributes,
                            HttpSession session) {
        if (session.getAttribute("admin") == null) return "redirect:/admin/login";

        if (admin.getId() == null || admin.getId().isEmpty()) {
            // ── CREATE new admin ──
            adminService.createAdmin(admin);
            redirectAttributes.addFlashAttribute("message", "Admin created successfully!");
        } else {
            // ── UPDATE existing admin ──
            // if password field was left empty, keep the existing password
            if (admin.getPassword() == null || admin.getPassword().isEmpty()) {
                AdminUser existing = adminService.getAllAdmins().stream()
                        .filter(a -> a.getId().equals(admin.getId()))
                        .findFirst().orElse(null);
                if (existing != null) admin.setPassword(existing.getPassword());
            }
            adminService.updateAdmin(admin);
            redirectAttributes.addFlashAttribute("message", "Admin updated successfully!");
        }
        return "redirect:/admin/admins";
    }

    @GetMapping("/admins/delete/{id}")
    public String deleteAdmin(@PathVariable String id,
                              RedirectAttributes redirectAttributes,
                              HttpSession session) {
        if (session.getAttribute("admin") == null) return "redirect:/admin/login";
        adminService.deleteAdmin(id);
        redirectAttributes.addFlashAttribute("message", "Admin deleted successfully!");
        return "redirect:/admin/admins";
    }
}