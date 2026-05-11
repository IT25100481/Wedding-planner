package com.wedding.planner.service;

import com.wedding.planner.model.User;
import org.springframework.stereotype.Service;
import java.io.*;
import java.nio.file.*;
import java.util.stream.Stream;

@Service
public class UserService {
    private final String FILE_PATH = "src/main/resources/data/users.txt";

    public void saveUser(User user) {
        try {
            Files.createDirectories(Paths.get("src/main/resources/data"));
            String data = String.format("%s,%s,%s,%s%n",
                    user.getFullName(), user.getEmail(), user.getPassword(), user.getRole());
            Files.write(Paths.get(FILE_PATH), data.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) { e.printStackTrace(); }
    }

    public User findByEmail(String email) {
        try {
            Path path = Paths.get(FILE_PATH);
            if (!Files.exists(path)) return null;
            try (Stream<String> lines = Files.lines(path)) {
                return lines.map(line -> line.split(","))
                        .filter(parts -> parts.length >= 4 && parts[1].equals(email))
                        .map(parts -> new User(parts[0], parts[1], parts[2], parts[3]))
                        .findFirst().orElse(null);
            }
        } catch (IOException e) { return null; }
    }

    public boolean authenticate(String email, String password) {
        User user = findByEmail(email);
        return user != null && user.getPassword().equals(password);
    }
}