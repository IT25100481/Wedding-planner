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

    private final String FILE_PATH = "services.txt";
    private final String UPLOAD_DIR = "src/main/resources/static/uploads/";

    @GetMapping("/vendor/dashboard")
    public String showDashboard(Model model) {
        List<Service> services = getAllServices();
        model.addAttribute("services", services);
        return "vendor-dashboard";
    }

    @PostMapping("/vendor/add-service")
    public String addService(@ModelAttribute Service service,
                             @RequestParam(value = "otherCategory", required = false) String otherCategory,
                             @RequestParam("imageFile") MultipartFile file) {
        try {
            // Handle 'Other' category selection
            if ("Other".equalsIgnoreCase(service.getCategory()) && otherCategory != null && !otherCategory.isEmpty()) {
                service.setCategory(otherCategory);
            }

            // NORMALIZATION: Convert to lowercase to match JavaScript filters
            if (service.getCategory() != null) {
                service.setCategory(service.getCategory().toLowerCase().trim());
            }
            if (service.getTradition() != null) {
                service.setTradition(service.getTradition().toLowerCase().trim());
            }

            // Generate unique ID
            service.setId(UUID.randomUUID().toString());

            // Set status to APPROVED so it shows on the customer page instantly
            service.setStatus("APPROVED");

            String fileName = "no-image.jpg";
            if (!file.isEmpty()) {
                fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
                File uploadDir = new File(UPLOAD_DIR);
                if (!uploadDir.exists()) uploadDir.mkdirs();

                Path path = Paths.get(UPLOAD_DIR + fileName);
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            }

            service.setImagePath(fileName);
            saveServiceToFile(service);

            return "redirect:/vendor/dashboard?success";
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

    @PostMapping("/vendor/update")
    public String updateService(@ModelAttribute Service updatedService) {
        List<Service> services = getAllServices();
        for (int i = 0; i < services.size(); i++) {
            if (services.get(i).getId().equals(updatedService.getId())) {
                // Preserve existing image and status if not modified
                updatedService.setImagePath(services.get(i).getImagePath());
                updatedService.setStatus(services.get(i).getStatus());

                // Maintain lowercase normalization on update
                if (updatedService.getCategory() != null) {
                    updatedService.setCategory(updatedService.getCategory().toLowerCase().trim());
                }
                if (updatedService.getTradition() != null) {
                    updatedService.setTradition(updatedService.getTradition().toLowerCase().trim());
                }

                services.set(i, updatedService);
                break;
            }
        }
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
                if (s != null) {
                    services.add(s);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return services;
    }

    private void saveServiceToFile(Service s) {
        try (FileWriter fw = new FileWriter(FILE_PATH, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            out.println(formatServiceLine(s));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void rewriteFile(List<Service> services) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_PATH))) {
            for (Service s : services) {
                pw.println(formatServiceLine(s));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String formatServiceLine(Service s) {
        return String.join("|",
                (s.getId() != null ? s.getId() : ""),
                (s.getBusinessName() != null ? s.getBusinessName() : ""),
                (s.getCategory() != null ? s.getCategory() : ""),
                (s.getTradition() != null ? s.getTradition() : ""),
                (s.getDescription() != null ? s.getDescription() : ""),
                (s.getContact() != null ? s.getContact() : ""),
                (s.getPrice() != null ? s.getPrice() : ""),
                (s.getImagePath() != null ? s.getImagePath() : "no-image.jpg"),
                (s.getStatus() != null ? s.getStatus() : "APPROVED")
        );
    }
}