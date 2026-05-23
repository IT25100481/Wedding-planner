package com.wedding.planner.controller;

import com.wedding.planner.model.Inquiry;
import com.wedding.planner.service.InquiryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes; // Added import

@Controller
public class InquiryController {

    @Autowired
    private InquiryService inquiryService;

    @GetMapping("/inquiry")
    public String showInquiryForm(Model model) {
        // Prevent overwriting the model if we are returning from a successful submission redirect
        if (!model.containsAttribute("inquiry")) {
            model.addAttribute("inquiry", new Inquiry());
        }
        return "inquiry";
    }

    @PostMapping("/submit-inquiry")
    public String submitInquiry(@ModelAttribute("inquiry") Inquiry inquiry,
                                @RequestParam("customUserTypedEmail") String typedEmail,
                                RedirectAttributes redirectAttributes) { // Added RedirectAttributes here

        // Directly maps the selected dropdown userRole and typedEmail to service (Untouched logic)
        inquiryService.saveInquiry(inquiry, typedEmail);

        // Pass the precise success notification string to the redirected page
        redirectAttributes.addFlashAttribute("successMessage", "Inquiry is submitted successfully!");

        return "redirect:/inquiry";
    }
}