package com.wedding.planner.service;

import com.wedding.planner.model.Inquiry;
import org.springframework.stereotype.Service;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class InquiryService {
    private final String FILE_PATH = "inquiries.txt";

    // Writes inquiry data to the text file (Append Mode)
    public void saveInquiry(Inquiry inquiry, String typedEmail) {
        String vendorName = inquiry.getVendorName() != null && !inquiry.getVendorName().isEmpty()
                ? inquiry.getVendorName() : "General Inquiry";

        // Creating a Comma-Separated Values (CSV) record
        String record = typedEmail + "," +
                inquiry.getCustomerName() + "," +
                inquiry.getContactNo() + "," +
                inquiry.getWeddingDate() + "," +
                inquiry.getMessage() + "," +
                vendorName;

        // Try-with-resources automatically closes streams to prevent data corruption
        try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(FILE_PATH, true)))) {
            pw.println(record);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Reads all records from the text file line-by-line
    public List<Inquiry> getAllInquiries() {
        List<Inquiry> allInquiries = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(","); // Tokenizing string by commas
                if (data.length >= 5) {
                    Inquiry inq = new Inquiry();
                    inq.setCustomerEmail(data[0]);
                    inq.setCustomerName(data[1]);
                    inq.setContactNo(data[2]);
                    inq.setWeddingDate(data[3]);
                    inq.setMessage(data[4]);

                    if (data.length >= 6 && data[5] != null && !data[5].isEmpty()) {
                        inq.setVendorName(data[5]);
                    } else {
                        inq.setVendorName("General Inquiry");
                    }
                    allInquiries.add(inq);
                }
            }
        } catch (IOException e) {
            System.out.println("Inquiry storage file is empty or not yet created.");
        }
        return allInquiries;
    }
}