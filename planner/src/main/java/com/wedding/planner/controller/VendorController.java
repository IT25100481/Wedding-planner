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
        // Just show everything in the file immediately
        model.addAttribute("services", getAllServices());
        return "vendor-dashboard";
    }

    @PostMapping("/vendor/add-service")
    public String addService(@ModelAttribute Service service,
                             @RequestParam("imageFile") MultipartFile file) {
        try {
            service.setId(UUID.randomUUID().toString());
            service.setOwnerEmail("test1email"); // Hardcoded to keep your file columns aligned
            service.setStatus("APPROVED");

            String fileName = "no-image.jpg";
            if (!file.isEmpty()) {
                fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
                Path path = Paths.get(UPLOAD_DIR + fileName);
                if (!Files.exists(path.getParent())) Files.createDirectories(path.getParent());
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            }

            service.setImagePath(fileName);
            saveServiceToFile(service);
            return "redirect:/vendor/dashboard";
        } catch (IOException e) {
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
                updatedService.setOwnerEmail("test1email");
                updatedService.setImagePath(services.get(i).getImagePath());
                updatedService.setStatus(services.get(i).getStatus());
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
                if (s != null) services.add(s);
            }
        } catch (IOException e) { e.printStackTrace(); }
        return services;
    }

    private void saveServiceToFile(Service s) {
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(FILE_PATH, true)))) {
            out.println(s.toFileLine());
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void rewriteFile(List<Service> services) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_PATH))) {
            for (Service s : services) pw.println(s.toFileLine());
        } catch (IOException e) { e.printStackTrace(); }
    }
}