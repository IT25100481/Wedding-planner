package com.wedding.planner.controller;

public class AdminController {
    import com.wedding.planner.model.AdminUser;
import com.wedding.planner.service.AdminService;
import com.wedding.planner.service.VendorService; // Assumed service for vendors
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.List;

    /**
     * AdminController handles all requests coming to the /admin URL.
     * It manages security (login/logout), the dashboard, and CRUD for Admins and Vendors.
     */
    @Controller
    @RequestMapping("/admin")
    public class AdminController {

        //Services handle the actual data logic
        private final AdminService adminService;     //Handling Admin accounts and passwords
        private final VendorService vendorService;   //Managing vendors

        //Dependency Injection: Spring automatically provides the services when the controller is created
        @Autowired
        public AdminController(AdminService adminService, VendorService vendorService) {
            this.adminService = adminService;
            this.vendorService = vendorService;
        }


        // --- AUTHENTICATION SECTION ---

        //Displays the Login Page
        @GetMapping("/login")
        public String loginPage() {
            return "admin/login";
        }
}
