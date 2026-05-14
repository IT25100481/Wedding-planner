package com.wedding.planner.controller;

import com.wedding.planner.model.Service;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class VendorController {

    private final String FILE_PATH = "planner/services.txt";
    private final String UPLOAD_DIR = "src/main/resources/static/uploads/";
    private final String PROFILE_FILE = "planner/profile.txt";
    private final String BOOKINGS_FILE = "bookings.txt";

    @GetMapping("/vendor/dashboard")
    public String showDashboard(Model model) {
        List<Service> services = getAllServices();
        List<Map<String, String>> bookings = getBookings();

        double total = bookings.stream()
                .mapToDouble(b -> {
                    try {
                        return Double.parseDouble(b.get("price").replaceAll("[^0-9]", ""));
                    } catch (Exception e) { return 0; }
                })
                .sum();

        model.addAttribute("services", services);
        model.addAttribute("bookings", bookings);
        model.addAttribute("totalPayments", String.format("%,.0f", total));
        model.addAttribute("newService", new Service());
        return "vendors/vendor-dashboard";
    }

    @PostMapping("/vendor/add-service")
    public String addService(@ModelAttribute Service service,
                             @RequestParam("offeringType") String offeringType,
                             @RequestParam(value = "imageFile", required = false) MultipartFile file,
                             @RequestParam(value = "imageUrl", required = false) String imageUrl) {
        try {
            service.setId("v" + System.currentTimeMillis());
            service.setStatus("Available");

            if ("essential".equals(offeringType)) {
                service.setTradition("Universal");
            }

            if (imageUrl != null && !imageUrl.isEmpty()) {
                service.setImagePath(imageUrl);
            } else if (file != null && !file.isEmpty()) {
                String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
                Path path = Paths.get(UPLOAD_DIR + fileName);
                Files.createDirectories(path.getParent());
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                service.setImagePath(fileName);
            } else {
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

    @PostMapping("/vendor/update")
    public String updateService(@ModelAttribute Service updatedService) {
        List<Service> services = getAllServices();
        for (int i = 0; i < services.size(); i++) {
            if (services.get(i).getId().equals(updatedService.getId())) {
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

    @PostMapping("/vendor/update-profile")
    public String updateProfile(@RequestParam String name,
                                @RequestParam String location,
                                @RequestParam String contact,
                                @RequestParam String description,
                                @RequestParam(value = "profilePicFile", required = false) MultipartFile file) {
        try {
            String fileName = "default-profile.png";
            if (file != null && !file.isEmpty()) {
                fileName = "profile_" + System.currentTimeMillis() + "_" + file.getOriginalFilename();
                Path path = Paths.get(UPLOAD_DIR + fileName);
                Files.createDirectories(path.getParent());
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            }
            String profileData = name + "|" + location + "|" + contact + "|" + description + "|" + fileName;
            Files.write(Paths.get(PROFILE_FILE), profileData.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "redirect:/vendor/dashboard";
    }


    private List<Map<String, String>> getBookings() {
        Path path = Paths.get(BOOKINGS_FILE);
        if (!Files.exists(path)) return new ArrayList<>();
        try {
            return Files.lines(path)
                    .map(line -> line.split("\\|"))
                    .filter(parts -> parts.length >= 3)
                    .map(parts -> {
                        Map<String, String> b = new HashMap<>();
                        b.put("serviceName", parts[0]);
                        b.put("customerName", parts[1]);
                        b.put("price", parts[2]);
                        return b;
                    })
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
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