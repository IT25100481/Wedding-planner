package com.wedding.planner.service;

import com.wedding.planner.model.Review;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    private static final String FILE_PATH = "reviews.txt";
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    // ── CREATE: Add a new review ──
    public void addReview(Review review) {
        review.setId("REV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        review.setCreatedAt(LocalDateTime.now().format(FORMATTER));
        review.setStatus("PENDING");   // admin must approve before it shows publicly

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(review.toFileLine());
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ── READ: Load all reviews from file ──
    public List<Review> getAllReviews() {
        List<Review> reviews = new ArrayList<>();
        File file = new File(FILE_PATH);
        if (!file.exists()) return reviews;

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    Review r = Review.fromFileLine(line);
                    if (r != null) reviews.add(r);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return reviews;
    }

    // ── READ: Get only approved reviews (for public display) ──
    public List<Review> getApprovedReviews() {
        return getAllReviews().stream()
                .filter(r -> "APPROVED".equals(r.getStatus()))
                .collect(Collectors.toList());
    }

    // ── READ: Get reviews submitted by a specific user ──
    public List<Review> getReviewsByUser(String userId) {
        return getAllReviews().stream()
                .filter(r -> userId.equals(r.getUserId()))
                .collect(Collectors.toList());
    }

    // ── READ: Find a single review by its ID ──
    public Review getReviewById(String id) {
        return getAllReviews().stream()
                .filter(r -> id.equals(r.getId()))
                .findFirst()
                .orElse(null);
    }

    // ── UPDATE: Edit the comment/rating of an existing review ──
    public boolean updateReview(String id, int newRating, String newComment) {
        List<Review> reviews = getAllReviews();
        boolean found = false;
        for (Review r : reviews) {
            if (id.equals(r.getId())) {
                r.setRating(newRating);
                r.setComment(newComment);
                r.setStatus("PENDING");   // reset to pending after edit
                found = true;
                break;
            }
        }
        if (found) saveAll(reviews);
        return found;
    }

    // ── UPDATE: Admin approves or rejects a review ──
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

    // ── DELETE: Remove a review by ID ──
    public boolean deleteReview(String id) {
        List<Review> reviews = getAllReviews();
        int before = reviews.size();
        reviews.removeIf(r -> id.equals(r.getId()));
        if (reviews.size() < before) {
            saveAll(reviews);
            return true;
        }
        return false;
    }

    // ── HELPER: Overwrite file with current list ──
    private void saveAll(List<Review> reviews) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, false))) {
            for (Review r : reviews) {
                writer.write(r.toFileLine());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ── STAT: Average rating across all approved reviews ──
    public double getAverageRating() {
        List<Review> approved = getApprovedReviews();
        if (approved.isEmpty()) return 0.0;
        return approved.stream().mapToInt(Review::getRating).average().orElse(0.0);
    }
}
