package com.wedding.planner.service;

import com.wedding.planner.model.Review;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    // Always resolve to the correct folder no matter where the app runs from
    private static final String FILE_NAME = "reviews.txt";
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /** Returns the absolute path to reviews.txt, creating the file if needed */
    private String getFilePath() {
        // Try working directory first (where other .txt files like payments.txt live)
        File f = new File(FILE_NAME);
        if (!f.exists()) {
            try { f.createNewFile(); } catch (IOException ignored) {}
        }
        return f.getAbsolutePath();
    }

    // ── CREATE ────────────────────────────────────────────────────────────
    public void addReview(Review review) {
        review.setId("REV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        review.setCreatedAt(LocalDateTime.now().format(FORMATTER));
        review.setStatus("PENDING");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(getFilePath(), true))) {
            writer.write(review.toFileLine());
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ── READ: all reviews ─────────────────────────────────────────────────
    public List<Review> getAllReviews() {
        List<Review> reviews = new ArrayList<>();
        File file = new File(getFilePath());
        if (!file.exists() || file.length() == 0) return reviews;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    Review r = Review.fromFileLine(line);
                    if (r != null) reviews.add(r);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return reviews;
    }

    // ── READ: approved only (public page) ────────────────────────────────
    public List<Review> getApprovedReviews() {
        return getAllReviews().stream()
                .filter(r -> "APPROVED".equals(r.getStatus()))
                .collect(Collectors.toList());
    }

    // ── READ: by logged-in user ───────────────────────────────────────────
    public List<Review> getReviewsByUser(String userId) {
        return getAllReviews().stream()
                .filter(r -> userId != null && userId.equals(r.getUserId()))
                .collect(Collectors.toList());
    }

    // ── READ: single review by ID ─────────────────────────────────────────
    public Review getReviewById(String id) {
        return getAllReviews().stream()
                .filter(r -> id != null && id.equals(r.getId()))
                .findFirst().orElse(null);
    }

    // ── UPDATE: customer edits rating/comment ─────────────────────────────
    public boolean updateReview(String id, int newRating, String newComment) {
        List<Review> reviews = getAllReviews();
        boolean found = false;
        for (Review r : reviews) {
            if (id.equals(r.getId())) {
                r.setRating(newRating);
                r.setComment(newComment);
                r.setStatus("PENDING");
                found = true;
                break;
            }
        }
        if (found) saveAll(reviews);
        return found;
    }

    // ── UPDATE: admin changes status ──────────────────────────────────────
    public boolean updateStatus(String id, String newStatus) {
        List<Review> reviews = getAllReviews();
        boolean found = false;
        for (Review r : reviews) {
            if (id.equals(r.getId())) {
                r.setStatus(newStatus);
                found = true;
                break;
            }
        }
        if (found) saveAll(reviews);
        return found;
    }

    // ── DELETE ────────────────────────────────────────────────────────────
    public boolean deleteReview(String id) {
        List<Review> reviews = getAllReviews();
        int before = reviews.size();
        reviews.removeIf(r -> id.equals(r.getId()));
        if (reviews.size() < before) { saveAll(reviews); return true; }
        return false;
    }

    // ── HELPER: write full list back to file ──────────────────────────────
    private void saveAll(List<Review> reviews) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(getFilePath(), false))) {
            for (Review r : reviews) {
                writer.write(r.toFileLine());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ── STAT: average rating of approved reviews ──────────────────────────
    public double getAverageRating() {
        List<Review> approved = getApprovedReviews();
        if (approved.isEmpty()) return 0.0;
        return approved.stream().mapToInt(Review::getRating).average().orElse(0.0);
    }
}
