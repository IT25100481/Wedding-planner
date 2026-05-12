package com.wedding.planner.controller;

import com.wedding.planner.model.Service;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.*;
import java.util.*;

@Controller
public class CustomerVendorController {

    @GetMapping("/vendors")
    public String showCustomerVendors(@RequestParam(value = "style", required = false) String style, Model model) {
        List<Service> allServices = new ArrayList<>();

        String filePath = System.getProperty("user.dir") + File.separator + "services.txt";
        File file = new File(filePath);

        if (file.exists()) {
            System.out.println("VIVAHA INFO: services.txt found at: " + file.getAbsolutePath());
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                int count = 0;
                while ((line = br.readLine()) != null) {
                    if (line.trim().isEmpty()) continue;
                    Service s = Service.fromFileLine(line);
                    if (s != null && s.getStatus() != null && s.getStatus().toUpperCase().contains("APP")) {
                        allServices.add(s);
                        count++;
                    }
                }
                System.out.println("VIVAHA INFO: Successfully loaded " + count + " approved vendors.");
            } catch (IOException e) {
                System.err.println("VIVAHA ERROR: Could not read file content.");
                e.printStackTrace();
            }
        } else {
            System.err.println("VIVAHA ERROR: services.txt NOT FOUND at " + file.getAbsolutePath());
            System.err.println("ACTION REQUIRED: Move services.txt to the folder shown above.");
        }

        String currentStyle = (style == null || style.trim().isEmpty()) ? "Mixed" : style;

        model.addAttribute("allVendors", allServices);
        model.addAttribute("selectedStyle", currentStyle);

        return "customer-vendors";
    }
}