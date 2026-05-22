package com.wedding.planner.service;

import com.wedding.planner.model.Inquiry;
import org.springframework.stereotype.Service;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class InquiryService {
    private final String FILE_PATH = "inquiries.txt";

    public void saveInquiry(Inquiry inquiry, String typedEmail) {
        // Dropdown eke eka empty unoth default 'Guest' kiyala wathenne
        String role = (inquiry.getUserRole() != null && !inquiry.getUserRole().isEmpty())
                ? inquiry.getUserRole() : "Guest";

        // Layout: Email, Name, Contact, Message, VendorName, UserRole
        String record = typedEmail + "," +             // data[0]
                inquiry.getCustomerName() + "," +      // data[1]
                inquiry.getContactNo() + "," +         // data[2]
                inquiry.getMessage() + "," +           // data[3]
                inquiry.getVendorName() + "," +        // data[4]
                role;                                  // data[5]

        try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(FILE_PATH, true)))) {
            pw.println(record);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Inquiry> getAllInquiries() {
        List<Inquiry> allInquiries = new ArrayList<>();
        File file = new File(FILE_PATH);
        if (!file.exists()) return allInquiries;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] data = line.split(",");
                if (data.length >= 5) {
                    Inquiry inq = new Inquiry();
                    inq.setCustomerEmail(data[0].trim());
                    inq.setCustomerName(data[1].trim());
                    inq.setContactNo(data[2].trim());
                    inq.setMessage(data[3].trim());
                    inq.setVendorName(data[4].trim());

                    if (data.length >= 6) {
                        inq.setUserRole(data[5].trim());
                    } else {
                        inq.setUserRole("Guest");
                    }
                    allInquiries.add(inq);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading inquiries file.");
        }
        return allInquiries;
    }
}