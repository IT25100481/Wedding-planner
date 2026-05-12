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
            // Logic for 'Other' category
            if ("Other".equalsIgnoreCase(service.getCategory()) && otherCategory != null && !otherCategory.isEmpty()) {
                service.setCategory(otherCategory);
            }

            service.setId(UUID.randomUUID().toString());

            // Image handling
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

    @PostMapping("/vendor/update")
    public String updateService(@ModelAttribute Service updatedService) {
        List<Service> services = getAllServices();
        for (int i = 0; i < services.size(); i++) {
            if (services.get(i).getId().equals(updatedService.getId())) {
                updatedService.setImagePath(services.get(i).getImagePath());
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
                String[] parts = line.split(",");
                if (parts.length >= 7) {
                    Service s = new Service(parts[0], parts[1], parts[2], parts[3], parts[4], parts[5], parts[6]);
                    if (parts.length == 8) {
                        s.setImagePath(parts[7]);
                    }
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
            out.println(s.getId() + "," + s.getBusinessName() + "," + s.getCategory() + "," +
                    s.getTradition() + "," + s.getDescription() + "," + s.getContact() + "," +
                    s.getPrice() + "," + s.getImagePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void rewriteFile(List<Service> services) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_PATH))) {
            for (Service s : services) {
                pw.println(s.getId() + "," + s.getBusinessName() + "," + s.getCategory() + "," +
                        s.getTradition() + "," + s.getDescription() + "," + s.getContact() + "," +
                        s.getPrice() + "," + (s.getImagePath() != null ? s.getImagePath() : ""));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}