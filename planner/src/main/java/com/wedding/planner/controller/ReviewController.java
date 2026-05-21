package com.wedding.planner.controller;

import com.wedding.planner.model.Review;
import com.wedding.planner.service.ReviewService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    // ════════════════════════════════════════════════════════
    //  CUSTOMER ROUTES
    // ════════════════════════════════════════════════════════

    /** GET /reviews/submit — show the form */
    @GetMapping("/reviews/submit")
    public String showSubmitForm(
            @RequestParam(required = false, defaultValue = "") String vendorName,
            @RequestParam(required = false, defaultValue = "") String bookingId,
            Model model, HttpSession session, RedirectAttributes ra) {

        if (session.getAttribute("loggedInUser") == null) {
            ra.addFlashAttribute("error", "Please log in to leave a review.");
            return "redirect:/login";
        }

        // Pre-fill vendor from payment page if provided
        model.addAttribute("prefilledVendor", vendorName);
        model.addAttribute("prefilledBookingId", bookingId);
        return "reviews/submit-review";
    }

    /** POST /reviews/submit — save the review */
    @PostMapping("/reviews/submit")
    public String submitReview(
            @RequestParam(defaultValue = "0") int rating,
            @RequestParam(defaultValue = "") String serviceType,
            @RequestParam(defaultValue = "") String vendorName,
            @RequestParam(defaultValue = "") String comment,
            HttpSession session, RedirectAttributes ra) {

        String userId   = (String) session.getAttribute("loggedInUser");
        String fullName = (String) session.getAttribute("userName");
        if (userId == null) return "redirect:/login";

        // Validation
        if (comment.trim().isEmpty()) {
            ra.addFlashAttribute("error", "Please write a comment before submitting.");
            return "redirect:/reviews/submit";
        }
        if (rating < 1 || rating > 5) {
            ra.addFlashAttribute("error", "Please select a star rating (1–5).");
            return "redirect:/reviews/submit";
        }
        if (serviceType.trim().isEmpty()) {
            ra.addFlashAttribute("error", "Please select a service type.");
            return "redirect:/reviews/submit";
        }

        Review review = new Review();
        review.setUserId(userId);
        review.setFullName(fullName != null ? fullName : userId);
        review.setRating(rating);
        review.setServiceType(serviceType);
        review.setVendorName(vendorName);
        review.setComment(comment);

        reviewService.addReview(review);
        ra.addFlashAttribute("success",
                "Thank you! Your review has been submitted and is pending approval.");
        return "redirect:/reviews/my-reviews";
    }

    /** GET /reviews/my-reviews — customer sees their own reviews */
    @GetMapping("/reviews/my-reviews")
    public String myReviews(Model model, HttpSession session, RedirectAttributes ra) {
        String userId = (String) session.getAttribute("loggedInUser");
        if (userId == null) {
            ra.addFlashAttribute("error", "Please log in to view your reviews.");
            return "redirect:/login";
        }
        model.addAttribute("reviews", reviewService.getReviewsByUser(userId));
        return "reviews/my-reviews";
    }

    /** GET /reviews/edit/{id} — show pre-filled edit form */
    @GetMapping("/reviews/edit/{id}")
    public String showEditForm(@PathVariable String id, Model model,
                               HttpSession session, RedirectAttributes ra) {
        String userId = (String) session.getAttribute("loggedInUser");
        if (userId == null) return "redirect:/login";

        Review review = reviewService.getReviewById(id);
        if (review == null || !review.getUserId().equals(userId)) {
            ra.addFlashAttribute("error", "Review not found or access denied.");
            return "redirect:/reviews/my-reviews";
        }
        model.addAttribute("review", review);
        return "reviews/edit-review";
    }

    /** POST /reviews/edit/{id} — save edited review */
    @PostMapping("/reviews/edit/{id}")
    public String updateReview(@PathVariable String id,
                               @RequestParam(defaultValue = "0") int rating,
                               @RequestParam(defaultValue = "") String comment,
                               HttpSession session, RedirectAttributes ra) {
        String userId = (String) session.getAttribute("loggedInUser");
        if (userId == null) return "redirect:/login";

        Review review = reviewService.getReviewById(id);
        if (review == null || !review.getUserId().equals(userId)) {
            ra.addFlashAttribute("error", "Access denied.");
            return "redirect:/reviews/my-reviews";
        }
        if (comment.trim().isEmpty()) {
            ra.addFlashAttribute("error", "Comment cannot be empty.");
            return "redirect:/reviews/edit/" + id;
        }
        if (rating < 1 || rating > 5) {
            ra.addFlashAttribute("error", "Please select a star rating.");
            return "redirect:/reviews/edit/" + id;
        }

        reviewService.updateReview(id, rating, comment);
        ra.addFlashAttribute("success", "Your review has been updated and is pending re-approval.");
        return "redirect:/reviews/my-reviews";
    }

    /** POST /reviews/delete/{id} — customer deletes their own review */
    @PostMapping("/reviews/delete/{id}")
    public String deleteReview(@PathVariable String id,
                               HttpSession session, RedirectAttributes ra) {
        String userId = (String) session.getAttribute("loggedInUser");
        if (userId == null) return "redirect:/login";

        Review review = reviewService.getReviewById(id);
        if (review == null || !review.getUserId().equals(userId)) {
            ra.addFlashAttribute("error", "Access denied.");
            return "redirect:/reviews/my-reviews";
        }
        reviewService.deleteReview(id);
        ra.addFlashAttribute("success", "Your review has been deleted.");
        return "redirect:/reviews/my-reviews";
    }

    /** GET /reviews — public approved reviews page */
    @GetMapping("/reviews")
    public String publicReviews(Model model) {
        model.addAttribute("reviews", reviewService.getApprovedReviews());
        model.addAttribute("avgRating", reviewService.getAverageRating());
        return "reviews/public-reviews";
    }

    // ════════════════════════════════════════════════════════
    //  ADMIN ROUTES  — uses "admin" session key (matches AdminController)
    // ════════════════════════════════════════════════════════

    /** GET /admin/reviews — admin sees all reviews */
    @GetMapping("/admin/reviews")
    public String adminReviews(Model model, HttpSession session, RedirectAttributes ra) {
        // FIX: your AdminController sets "admin", not "adminLoggedIn"
        if (session.getAttribute("admin") == null) {
            ra.addFlashAttribute("error", "Admin access required.");
            return "redirect:/admin/login";
        }
        model.addAttribute("reviews", reviewService.getAllReviews());
        model.addAttribute("avgRating", reviewService.getAverageRating());
        return "reviews/admin-reviews";
    }

    /** POST /admin/reviews/approve/{id} */
    @PostMapping("/admin/reviews/approve/{id}")
    public String approveReview(@PathVariable String id,
                                HttpSession session, RedirectAttributes ra) {
        if (session.getAttribute("admin") == null) return "redirect:/admin/login";
        reviewService.updateStatus(id, "APPROVED");
        ra.addFlashAttribute("success", "Review approved successfully.");
        return "redirect:/admin/reviews";
    }

    /** POST /admin/reviews/reject/{id} */
    @PostMapping("/admin/reviews/reject/{id}")
    public String rejectReview(@PathVariable String id,
                               HttpSession session, RedirectAttributes ra) {
        if (session.getAttribute("admin") == null) return "redirect:/admin/login";
        reviewService.updateStatus(id, "REJECTED");
        ra.addFlashAttribute("success", "Review rejected.");
        return "redirect:/admin/reviews";
    }

    /** POST /admin/reviews/delete/{id} */
    @PostMapping("/admin/reviews/delete/{id}")
    public String adminDeleteReview(@PathVariable String id,
                                    HttpSession session, RedirectAttributes ra) {
        if (session.getAttribute("admin") == null) return "redirect:/admin/login";
        reviewService.deleteReview(id);
        ra.addFlashAttribute("success", "Review deleted.");
        return "redirect:/admin/reviews";
    }
}
