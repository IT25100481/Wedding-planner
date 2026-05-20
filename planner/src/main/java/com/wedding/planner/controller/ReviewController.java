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
    //  CLIENT-SIDE ROUTES
    // ════════════════════════════════════════════════════════

    /** Show the "Submit a Review" form (GET) */
    @GetMapping("/reviews/submit")
    public String showSubmitForm(@RequestParam(required = false, defaultValue = "") String vendorName,
                                 @RequestParam(required = false, defaultValue = "") String bookingId,
                                 Model model, HttpSession session,
                                 RedirectAttributes ra) {
        if (session.getAttribute("loggedInUser") == null) {
            ra.addFlashAttribute("error", "Please log in to leave a review.");
            return "redirect:/login";
        }
        Review review = new Review();
        // pre-fill vendor name and bookingId if coming from payment success page
        if (!vendorName.isEmpty()) review.setVendorName(vendorName);
        if (!bookingId.isEmpty())  review.setServiceType("Wedding Package");
        model.addAttribute("review", review);
        model.addAttribute("prefilledVendor", vendorName);
        return "reviews/submit-review";
    }

    /** Handle review form submission (POST) */
    @PostMapping("/reviews/submit")
    public String submitReview(@ModelAttribute Review review,
                               HttpSession session,
                               RedirectAttributes ra) {
        String userId = (String) session.getAttribute("loggedInUser");
        String fullName = (String) session.getAttribute("userName");
        if (userId == null) return "redirect:/login";

        review.setUserId(userId);
        review.setFullName(fullName != null ? fullName : userId);

        // Basic validation
        if (review.getComment() == null || review.getComment().trim().isEmpty()) {
            ra.addFlashAttribute("error", "Please write a comment before submitting.");
            return "redirect:/reviews/submit";
        }
        if (review.getRating() < 1 || review.getRating() > 5) {
            ra.addFlashAttribute("error", "Rating must be between 1 and 5.");
            return "redirect:/reviews/submit";
        }

        reviewService.addReview(review);
        ra.addFlashAttribute("success",
                "Thank you for your feedback! Your review is pending approval.");
        return "redirect:/reviews/my-reviews";
    }

    /** Customer views their own submitted reviews */
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

    /** Customer edits their own review — show pre-filled form (GET) */
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

    /** Handle edit form submission (POST) */
    @PostMapping("/reviews/edit/{id}")
    public String updateReview(@PathVariable String id,
                               @RequestParam int rating,
                               @RequestParam String comment,
                               HttpSession session,
                               RedirectAttributes ra) {
        String userId = (String) session.getAttribute("loggedInUser");
        if (userId == null) return "redirect:/login";

        Review review = reviewService.getReviewById(id);
        if (review == null || !review.getUserId().equals(userId)) {
            ra.addFlashAttribute("error", "Access denied.");
            return "redirect:/reviews/my-reviews";
        }

        if (comment == null || comment.trim().isEmpty()) {
            ra.addFlashAttribute("error", "Comment cannot be empty.");
            return "redirect:/reviews/edit/" + id;
        }

        reviewService.updateReview(id, rating, comment);
        ra.addFlashAttribute("success", "Your review has been updated and is pending re-approval.");
        return "redirect:/reviews/my-reviews";
    }

    /** Customer deletes their own review */
    @PostMapping("/reviews/delete/{id}")
    public String deleteReview(@PathVariable String id,
                               HttpSession session,
                               RedirectAttributes ra) {
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

    /** Public page: show all approved reviews */
    @GetMapping("/reviews")
    public String publicReviews(Model model) {
        model.addAttribute("reviews", reviewService.getApprovedReviews());
        model.addAttribute("avgRating", reviewService.getAverageRating());
        return "reviews/public-reviews";
    }

    // ════════════════════════════════════════════════════════
    //  ADMIN-SIDE ROUTES
    // ════════════════════════════════════════════════════════

    /** Admin: view all reviews */
    @GetMapping("/admin/reviews")
    public String adminReviews(Model model, HttpSession session, RedirectAttributes ra) {
        if (session.getAttribute("adminLoggedIn") == null) {
            ra.addFlashAttribute("error", "Admin access required.");
            return "redirect:/admin/login";
        }
        model.addAttribute("reviews", reviewService.getAllReviews());
        model.addAttribute("avgRating", reviewService.getAverageRating());
        return "reviews/admin-reviews";
    }

    /** Admin: approve a review */
    @PostMapping("/admin/reviews/approve/{id}")
    public String approveReview(@PathVariable String id, RedirectAttributes ra,
                                HttpSession session) {
        if (session.getAttribute("adminLoggedIn") == null) return "redirect:/admin/login";
        reviewService.updateStatus(id, "APPROVED");
        ra.addFlashAttribute("success", "Review approved.");
        return "redirect:/admin/reviews";
    }

    /** Admin: reject a review */
    @PostMapping("/admin/reviews/reject/{id}")
    public String rejectReview(@PathVariable String id, RedirectAttributes ra,
                               HttpSession session) {
        if (session.getAttribute("adminLoggedIn") == null) return "redirect:/admin/login";
        reviewService.updateStatus(id, "REJECTED");
        ra.addFlashAttribute("success", "Review rejected.");
        return "redirect:/admin/reviews";
    }

    /** Admin: delete any review */
    @PostMapping("/admin/reviews/delete/{id}")
    public String adminDeleteReview(@PathVariable String id, RedirectAttributes ra,
                                    HttpSession session) {
        if (session.getAttribute("adminLoggedIn") == null) return "redirect:/admin/login";
        reviewService.deleteReview(id);
        ra.addFlashAttribute("success", "Review deleted.");
        return "redirect:/admin/reviews";
    }
}
