package com.wedding.planner.controller;

import com.wedding.planner.model.Payment;
import com.wedding.planner.service.PaymentService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    // ════════════════════════════════════════════
    // CLIENT SIDE
    // ════════════════════════════════════════════

    @GetMapping("/payments")
    public String customerPayment(
            @RequestParam(required = false, defaultValue = "") String bookingId,
            @RequestParam(required = false, defaultValue = "") String totalAmount,
            @RequestParam(required = false, defaultValue = "") String vendorName,
            Model model, HttpSession session) {

        // ── AUTHORIZATION: block if not logged in ──
        if (session.getAttribute("loggedInUser") == null) {
            return "redirect:/login?redirect=payments";
        }

        Payment newPayment = new Payment();

        // ── INPUT VALIDATION: safely parse totalAmount ──
        if (!bookingId.isEmpty()) newPayment.setBookingId(bookingId);
        if (!vendorName.isEmpty()) newPayment.setVendorName(vendorName);
        if (!totalAmount.isEmpty()) {
            try {
                double amount = Double.parseDouble(totalAmount);
                if (amount < 0) amount = 0; // reject negative amounts
                newPayment.setTotalAmount(amount);
            } catch (NumberFormatException e) {
                // totalAmount was not a valid number — default to 0
                newPayment.setTotalAmount(0);
            }
        }

        // attach logged-in user
        String userId = (String) session.getAttribute("loggedInUser");
        if (userId != null) newPayment.setUserId(userId);

        model.addAttribute("newPayment", newPayment);
        return "payments/customer-payment";
    }

    @PostMapping("/payments/add")
    public String addPayment(@ModelAttribute Payment payment,
                             HttpSession session,
                             RedirectAttributes ra) {

        // ── AUTHORIZATION: block if not logged in ──
        if (session.getAttribute("loggedInUser") == null) {
            return "redirect:/login?redirect=payments";
        }

        // ── INPUT VALIDATION: check amounts are valid ──
        if (payment.getTotalAmount() <= 0) {
            ra.addFlashAttribute("error", "Invalid payment amount.");
            return "redirect:/payments";
        }

        if (payment.getAmountPaid() <= 0 || payment.getAmountPaid() > payment.getTotalAmount()) {
            ra.addFlashAttribute("error", "Amount paid must be between 1 and the total due.");
            return "redirect:/payments";
        }

        // attach userId from session (never trust form input for this)
        String userId = (String) session.getAttribute("loggedInUser");
        if (userId != null) payment.setUserId(userId);

        // ── EXCEPTION HANDLING: catch file save failures ──
        try {
            paymentService.addPayment(payment);
            ra.addFlashAttribute("payment", payment);
            return "redirect:/payments/success";
        } catch (Exception e) {
            // payment failed to save — tell the user instead of silently failing
            ra.addFlashAttribute("error", "Payment could not be saved. Please try again.");
            return "redirect:/payments";
        }
    }

    @GetMapping("/payments/success")
    public String paymentSuccess(Model model) {
        if (!model.containsAttribute("payment")) {
            model.addAttribute("payment", new Payment());
        }
        return "payments/payment-success";
    }

    // ════════════════════════════════════════════
    // ADMIN SIDE
    // ════════════════════════════════════════════

    private boolean isAdmin(HttpSession session) {
        return session.getAttribute("admin") != null;
    }

    @GetMapping("/admin/payments")
    public String adminPayments(Model model, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/admin/login";
        // ── EXCEPTION HANDLING: catch file read failures ──
        try {
            model.addAttribute("payments", paymentService.getAllPayments());
            model.addAttribute("totalEarnings", paymentService.getTotalEarnings());
        } catch (Exception e) {
            model.addAttribute("payments", java.util.Collections.emptyList());
            model.addAttribute("totalEarnings", 0.0);
            model.addAttribute("error", "Could not load payment records.");
        }
        return "admin/payments";
    }

    @GetMapping("/admin/payments/delete/{id}")
    public String adminDelete(@PathVariable String id,
                              HttpSession session, RedirectAttributes ra) {
        if (!isAdmin(session)) return "redirect:/admin/login";
        // ── EXCEPTION HANDLING: catch delete failures ──
        try {
            paymentService.deletePayment(id);
            ra.addFlashAttribute("message", "Payment deleted successfully.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Could not delete payment. Please try again.");
        }
        return "redirect:/admin/payments";
    }

    @GetMapping("/admin/payments/edit/{id}")
    public String adminEditForm(@PathVariable String id,
                                Model model, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/admin/login";
        // ── EXCEPTION HANDLING: handle missing payment ──
        try {
            Payment payment = paymentService.getPaymentById(id);
            if (payment == null) {
                return "redirect:/admin/payments?error=notfound";
            }
            model.addAttribute("payment", payment);
        } catch (Exception e) {
            return "redirect:/admin/payments?error=notfound";
        }
        return "payments/payment-edit";
    }

    @PostMapping("/payments/update")
    public String updatePayment(@ModelAttribute Payment payment,
                                HttpSession session, RedirectAttributes ra) {
        if (!isAdmin(session)) return "redirect:/admin/login";
        // ── EXCEPTION HANDLING: catch update failures ──
        try {
            paymentService.updatePayment(payment);
            ra.addFlashAttribute("message", "Payment updated successfully.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Could not update payment. Please try again.");
        }
        return "redirect:/admin/payments";
    }
}