package com.wedding.planner.controller;

import com.wedding.planner.model.AdminUser;
import com.wedding.planner.model.Inquiry;
import com.wedding.planner.service.AdminService;
import com.wedding.planner.service.UserService;
import com.wedding.planner.service.VendorService;
import com.wedding.planner.service.InquiryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

//Web Application Controller
@Controller
@RequestMapping("/admin")
public class AdminController {

    //Attributes
    private final AdminService adminService;
    private final VendorService vendorService;
    private final InquiryService inquiryService;

    @Autowired
    private UserService userService;

    // Corrected Parameterized Constructor
    @Autowired
    public AdminController(AdminService adminService, VendorService vendorService, InquiryService inquiryService) {
        this.adminService = adminService;
        this.vendorService = vendorService;
        this.inquiryService = inquiryService;
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

    //Fetch and Display the list of registered vendors
    @GetMapping("/vendors")
    public String listVendors(Model model, HttpSession session) {
        if (session.getAttribute("admin") == null) return "redirect:/admin/login";
        model.addAttribute("vendors", vendorService.getAllVendors());
        return "admin/vendor-list";
    }

    //Approve Vendor
    @PostMapping("/vendors/approve/{id}")
    public String approveVendor(@PathVariable String id,
                                RedirectAttributes ra,
                                HttpSession session) {
        if (session.getAttribute("admin") == null) return "redirect:/admin/login";
        vendorService.updateStatus(id, "APPROVED");
        ra.addFlashAttribute("message", "Vendor approved successfully!");
        return "redirect:/admin/vendors";
    }

    // ── INQUIRY MANAGEMENT (FIXED & PROTECTED FROM 500 ERRORS) ──
    @GetMapping("/inquiries")
    public String listInquiries(Model model, HttpSession session) {
        // 1. Guard Clause: Redirect to login page if user session is unauthorized
        if (session.getAttribute("admin") == null) return "redirect:/admin/login";

        try {
            // 2. Fetch the absolute total list of all inquiries directly
            List<Inquiry> allInquiries = inquiryService.getAllInquiries();

            if (allInquiries == null) {
                allInquiries = new ArrayList<>();
            }

            // 3. Bind the combined collection safely
            model.addAttribute("allInquiries", allInquiries);
        } catch (Exception e) {
            System.out.println("Gracefully caught mapping read error to prevent 500 crashes.");
            model.addAttribute("allInquiries", new ArrayList<Inquiry>());
        }

        // 4. Render the master single table template view
        return "admin/inquiries-list";
    }

    // ── ADMIN CRUD ──
    //Reject Vendor
    @PostMapping("/vendors/reject/{id}")
    public String rejectVendor(@PathVariable String id,
                               RedirectAttributes ra,
                               HttpSession session) {
        if (session.getAttribute("admin") == null) return "redirect:/admin/login";
        vendorService.updateStatus(id, "REJECTED");
        ra.addFlashAttribute("message", "Vendor rejected.");
        return "redirect:/admin/vendors";
    }

    //Add Vendor
    @PostMapping("/vendors/add")
    public String addVendor(@ModelAttribute com.wedding.planner.model.Service vendor,
                            RedirectAttributes ra,
                            HttpSession session) {
        if (session.getAttribute("admin") == null) return "redirect:/admin/login";
        vendorService.addVendor(vendor);
        ra.addFlashAttribute("message", "Vendor added successfully!");
        return "redirect:/admin/vendors";
    }

    //Edit Vendor
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

    //Update Vendor
    @PostMapping("/vendors/update")
    public String updateVendor(@ModelAttribute com.wedding.planner.model.Service vendor,
                               RedirectAttributes ra,
                               HttpSession session) {
        if (session.getAttribute("admin") == null) return "redirect:/admin/login";
        vendorService.updateVendor(vendor);
        ra.addFlashAttribute("message", "Vendor updated successfully!");
        return "redirect:/admin/vendors";
    }

    //Delete Vendor
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

    //Delete Client by username and redirects back to dashboard
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

    //Fetch Admin records and display the list of Admins
    @GetMapping("/admins")
    public String listAdmins(Model model, HttpSession session) {
        if (session.getAttribute("admin") == null) return "redirect:/admin/login";
        model.addAttribute("admins", adminService.getAllAdmins());
        return "admin/admins";
    }

    //Add Admin
    @GetMapping("/admins/add")
    public String addAdminForm(Model model, HttpSession session) {
        if (session.getAttribute("admin") == null) return "redirect:/admin/login";
        model.addAttribute("admin", new AdminUser());
        return "admin/admin-form";
    }

    //Update Admin
    @PostMapping("/admins/save")
    public String saveAdmin(@ModelAttribute AdminUser admin,
                            RedirectAttributes redirectAttributes,
                            HttpSession session) {
        if (session.getAttribute("admin") == null) return "redirect:/admin/login";
        if (admin.getId() == null || admin.getId().isEmpty()) {
            adminService.createAdmin(admin);
            redirectAttributes.addFlashAttribute("message", "Admin created successfully!");
        } else {

            // ── UPDATE existing admin ──
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

    //Delete Admin
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