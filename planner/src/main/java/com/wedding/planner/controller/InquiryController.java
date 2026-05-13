package com.wedding.planner.controller;

import com.wedding.planner.model.Inquiry;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import jakarta.servlet.http.HttpSession;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Controller
public class InquiryController {

    // 1. User ta inquiry form eka pennana kalla
    @GetMapping("/inquiry")
    public String showInquiryPage(HttpSession session, Model model) {
        // Session eken check karanawa user log welada kiyala
        Object user = session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/login";
        }
        // Form ekata empty Inquiry object ekak yawanawa
        model.addAttribute("inquiry", new Inquiry());
        return "inquiry";
    }

    // 2. Form eka submit kalama file ekata save karana kalla
    @PostMapping("/submit-inquiry")
    public String handleInquiry(@ModelAttribute Inquiry inquiry, HttpSession session) {
        // Log wela inna userge email eka session eken gannawa
        String userEmail = (String) session.getAttribute("loggedInUser");

        if (userEmail == null) {
            return "redirect:/login";
        }

        // CSV format ekata record eka hadagannawa
        String record = userEmail + "," +
                inquiry.getCustomerName() + "," +
                inquiry.getContactNo() + "," +
                inquiry.getWeddingDate() + "," +
                inquiry.getMessage();

        // Text file ekata append karanawa
        try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter("inquiries.txt", true)))) {
            pw.println(record);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "redirect:/?success=true";
    }

    // 3. ADMIN TA DATA PENNANNA (Manual URL eken yanna: /admin/view-inquiries)
    @GetMapping("/admin/view-inquiries")
    public String adminViewInquiries(HttpSession session, Model model) {
        // Admin kenekda kiyala check karana logic ekak thiyanwa nam methanata danna

        List<Inquiry> inquiryList = new ArrayList<>();

        // Text file eka kiyawala list ekakata dagannawa
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
                    inquiryList.add(inq);
                }
            }
        } catch (IOException e) {
            System.out.println("Inquiry file eka thama hadila naha.");
        }

        // Thymeleaf table ekata data tika yawannawa
        model.addAttribute("allInquiries", inquiryList);

        // Templates/admin/inquiries-list.html file ekata redirect karanawa
        return "admin/inquiries-list";
    }
}