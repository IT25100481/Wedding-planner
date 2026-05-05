package com.wedding.planner.controller;

import com.wedding.planner.model.Inquiry;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

@Controller
public class InquiryController {

    @PostMapping("/submit-inquiry")
    public String handleInquiry(@ModelAttribute Inquiry inquiry) {
        String record = "Name: " + inquiry.getCustomerName() +
                ", Contact: " + inquiry.getContactNo() +
                ", Date: " + inquiry.getWeddingDate() +
                ", Message: " + inquiry.getMessage();

        try {

            File file = new File("inquiries.txt");
            FileWriter fw = new FileWriter(file, true); // true = append mode
            PrintWriter pw = new PrintWriter(fw);

            pw.println(record);
            pw.close();

            System.out.println("FILE SAVED AT: " + file.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "redirect:/?success=true";
    }
}