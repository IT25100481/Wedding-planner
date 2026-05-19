package com.wedding.planner.controller;

import com.wedding.planner.model.Inquiry;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.servlet.http.HttpSession;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Controller
public class InquiryController {

    @GetMapping("/inquiry")
    public String showInquiryPage(HttpSession session, Model model) {
        String username = (String) session.getAttribute("loggedInUser");
        if (username == null) {
            return "redirect:/login";
        }
        Inquiry inquiry = new Inquiry();
        model.addAttribute("inquiry", inquiry);
        return "inquiry";
    }

    @PostMapping("/submit-inquiry")
    public String handleInquiry(@ModelAttribute Inquiry inquiry,
                                @RequestParam("customUserTypedEmail") String typedEmail,
                                HttpSession session) {
        String username = (String) session.getAttribute("loggedInUser");
        if (username == null) {
            return "redirect:/login";
        }

        String vendorName = inquiry.getVendorName() != null && !inquiry.getVendorName().isEmpty()
                ? inquiry.getVendorName() : "General Inquiry";

        String record = typedEmail + "," +
                inquiry.getCustomerName() + "," +
                inquiry.getContactNo() + "," +
                inquiry.getWeddingDate() + "," +
                inquiry.getMessage() + "," +
                vendorName;

        try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter("inquiries.txt", true)))) {
            pw.println(record);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "redirect:/?success=true";
    }

    @GetMapping("/admin/view-inquiries")
    public String adminViewInquiries(HttpSession session, Model model) {
        List<Inquiry> customerInquiries = new ArrayList<>();
        List<Inquiry> vendorInquiries = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader("inquiries.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 5) {
                    Inquiry inq = new Inquiry();
                    inq.setCustomerEmail(data[0]);
                    inq.setCustomerName(data[1]);
                    inq.setContactNo(data[2]);
                    inq.setWeddingDate(data[3]);
                    inq.setMessage(data[4]);

                    if (data.length >= 6 && data[5] != null && !data[5].isEmpty() && !data[5].trim().equals("General Inquiry") && !data[5].trim().equals("General Customer")) {
                        inq.setVendorName(data[5]);
                        vendorInquiries.add(inq);
                    } else {
                        inq.setVendorName("General Customer");
                        customerInquiries.add(inq);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Inquiry file eka thama hadila naha.");
        }

        model.addAttribute("customerInquiries", customerInquiries);
        model.addAttribute("vendorInquiries", vendorInquiries);
        return "admin/inquiries-list";
    }
}