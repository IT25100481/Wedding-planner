package com.wedding.planner.controller;

import com.wedding.planner.model.Service;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.*;
import java.util.*;

@Controller
public class VendorController {

    // Points to the data file in resources
    private final String FILE_PATH = "src/main/resources/data/services.txt";
    private final String UPLOAD_DIR = "src/main/resources/static/uploads/";

    @GetMapping("/vendor/dashboard")
    public String showDashboard(Model model) {
        List<Service> services = getAllServices();
        model.addAttribute("services", services);
        model.addAttribute("newService", new Service());
        return "vendors/vendor-dashboard"; // Updated path
    }

    @PostMapping("/vendor/add-service")
    public String addService(@ModelAttribute Service service,
                             @RequestParam("offeringType") String offeringType,
                             @RequestParam(value = "imageFile", required = false) MultipartFile file,
                             @RequestParam(value = "imageUrl", required = false) String imageUrl) {
        try {
            // Generate a unique ID and set default status
            service.setId("v" + System.currentTimeMillis());
            service.setStatus("Available");

            // Logic for Essential vs Tradition
            if ("essential".equals(offeringType)) {
                service.setTradition("Universal");
            }
            // If tradition-based, the category is captured from the text input in the modal

            // Image handling: Priority to Internet Link, then File Upload
            if (imageUrl != null && !imageUrl.isEmpty()) {
                service.setImagePath(imageUrl);
            } else if (file != null && !file.isEmpty()) {
                String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
                Path path = Paths.get(UPLOAD_DIR + fileName);
                Files.createDirectories(path.getParent());
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                service.setImagePath(fileName);
            } else {
                // Default fallback image
                service.setImagePath("https://images.unsplash.com/photo-1511795409834-ef04bbd61622?w=500");
            }

            saveServiceToFile(service);
            return "redirect:/vendor/dashboard";
        } catch (IOException e) {
            e.printStackTrace();
            return "redirect:/vendor/dashboard?error";
        }
    }

    @GetMapping("/vendor/delete/{id}")
    public String deleteService(@PathVariable String id) {
        List<Service> services = getAllServices();
        services.removeIf(s -> s.getId().equals(id));
        rewriteFile(services);
        return "redirect:/vendor/dashboard";
    }

    private List<Service> getAllServices() {
        List<Service> services = new ArrayList<>();
        File file = new File(FILE_PATH);
        if (!file.exists()) return services;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                Service s = Service.fromFileLine(line);
                if (s != null) services.add(s);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return services;
    }

    private void saveServiceToFile(Service s) {
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(FILE_PATH, true)))) {
            out.println(s.toFileLine());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @PostMapping("/vendor/update")
    public String updateService(@ModelAttribute Service updatedService) {
        List<Service> services = getAllServices();
        for (int i = 0; i < services.size(); i++) {
            if (services.get(i).getId().equals(updatedService.getId())) {
                // Keep the old image path so it doesn't get lost
                updatedService.setImagePath(services.get(i).getImagePath());
                updatedService.setStatus(services.get(i).getStatus());
                updatedService.setCategory(services.get(i).getCategory());
                updatedService.setTradition(services.get(i).getTradition());

                services.set(i, updatedService);
                break;
            }
        }
        rewriteFile(services);
        return "redirect:/vendor/dashboard";
    }

    private void rewriteFile(List<Service> services) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_PATH))) {
            for (Service s : services) {
                pw.println(s.toFileLine());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}