package com.wedding.planner.controller;

import com.wedding.planner.model.Inquiry;
import com.wedding.planner.service.InquiryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Controller
public class InquiryController {

    @Autowired
    private InquiryService inquiryService; // Dependency Injection of Service Layer

    // Renders the Inquiry form page
    @GetMapping("/inquiry")
    public String showInquiryPage(HttpSession session, Model model) {
        String username = (String) session.getAttribute("loggedInUser");
        if (username == null) {
            return "redirect:/login"; // Route Protection/Security Check
        }
        Inquiry inquiry = new Inquiry();
        model.addAttribute("inquiry", inquiry);
        return "inquiry";
    }

    // Processes the submitted Inquiry form
    @PostMapping("/submit-inquiry")
    public String handleInquiry(@ModelAttribute Inquiry inquiry,
                                @RequestParam("customUserTypedEmail") String typedEmail,
                                HttpSession session) {
        String username = (String) session.getAttribute("loggedInUser");
        if (username == null) {
            return "redirect:/login";
        }

        // Delegating File Writing task to the Service Layer
        inquiryService.saveInquiry(inquiry, typedEmail);

        return "redirect:/?success=true";
    }

    // Filters and presents data to Admin Dashboard
    @GetMapping("/admin/view-inquiries")
    public String adminViewInquiries(HttpSession session, Model model) {
        String username = (String) session.getAttribute("loggedInUser");
        if (username == null) {
            return "redirect:/login";
        }

        List<Inquiry> customerInquiries = new ArrayList<>();
        List<Inquiry> vendorInquiries = new ArrayList<>();

        // Fetching all raw records from text file via Service
        List<Inquiry> allInquiries = inquiryService.getAllInquiries();

        // Categorization Logic: Separating General inquiries from Vendor inquiries
        for (Inquiry inq : allInquiries) {
            String vName = inq.getVendorName();
            if (vName != null && !vName.trim().equals("General Inquiry") && !vName.trim().equals("General Customer")) {
                vendorInquiries.add(inq);
            } else {
                inq.setVendorName("General Customer");
                customerInquiries.add(inq);
            }
        }

        model.addAttribute("customerInquiries", customerInquiries);
        model.addAttribute("vendorInquiries", vendorInquiries);
        return "admin/inquiries-list";
    }
}