package com.wedding.planner.controller;

import com.wedding.planner.model.Service;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.io.*;
import java.util.*;

@Controller
public class VendorCustomerController {

    private final String FILE_PATH = "services.txt";

    @GetMapping("/vendors")
    public String showVendorMarketplace(Model model) {
        List<Service> allServices = loadServices();

        // Essentials (Shown to everyone)
        model.addAttribute("photography", filterByCategory(allServices, "Photography"));
        model.addAttribute("cakes", filterByCategory(allServices, "Cake"));
        model.addAttribute("venues", filterByCategory(allServices, "Venue"));
        model.addAttribute("makeup", filterByCategory(allServices, "Makeup"));
        model.addAttribute("entertainment", filterByCategory(allServices, "Entertainment"));

        // Decorations
        List<Service> allDeco = new ArrayList<>();
        for (Service s : allServices) {
            if (s.getCategory().contains("Decoration")) allDeco.add(s);
        }
        model.addAttribute("decorations", allDeco);

        // Pass the full list for the "Curated for You" JS logic
        model.addAttribute("allServices", allServices);

        return "vendors/category-view";
    }

    private List<Service> loadServices() {
        List<Service> services = new ArrayList<>();
        File file = new File(FILE_PATH);
        if (!file.exists()) return services;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                Service s = Service.fromFileLine(line);
                if (s != null) {
                    services.add(s);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return services;
    }

    private List<Service> filterByCategory(List<Service> list, String category) {
        List<Service> filtered = new ArrayList<>();
        for (Service s : list) {
            if (s.getCategory().equalsIgnoreCase(category)) {
                filtered.add(s);
            }
        }
        return filtered;
    }
}