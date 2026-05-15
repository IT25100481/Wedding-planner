package com.wedding.planner.service;

import com.wedding.planner.model.Inquiry;
import org.springframework.stereotype.Service;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class InquiryService {
    private final String FILE_PATH = "inquiries.txt";

    public void saveToFile(Inquiry inquiry) {
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(FILE_PATH, true)))) {
            out.println(inquiry.getCustomerName() + "," + inquiry.getContactNo() + "," + inquiry.getWeddingDate() + "," + inquiry.getMessage());
        } catch (IOException e) { e.printStackTrace(); }
    }

    public List<Inquiry> getAllInquiries() {
        List<Inquiry> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if(data.length >= 4) {
                    Inquiry inq = new Inquiry();
                    inq.setCustomerName(data[0]);
                    inq.setContactNo(data[1]);
                    inq.setWeddingDate(data[2]);
                    inq.setMessage(data[3]);
                    list.add(inq);
                }
            }
        } catch (Exception e) { }
        return list;
    }
}