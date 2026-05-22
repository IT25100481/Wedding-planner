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

@Controller
public class InquiryController {

    @Autowired
    private InquiryService inquiryService;

    @GetMapping("/inquiry")
    public String showInquiryForm(Model model) {
        model.addAttribute("inquiry", new Inquiry());
        return "inquiry";
    }

    @PostMapping("/submit-inquiry")
    public String submitInquiry(@ModelAttribute("inquiry") Inquiry inquiry,
                                @RequestParam("customUserTypedEmail") String typedEmail) {

        // Directly maps the selected dropdown userRole and typedEmail to service
        inquiryService.saveInquiry(inquiry, typedEmail);
        return "redirect:/inquiry?success";
    }
}