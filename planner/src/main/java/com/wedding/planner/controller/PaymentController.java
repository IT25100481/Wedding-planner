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

    // Show checkout page
    @GetMapping("/payments")
    public String customerPayment(
            @RequestParam(required = false, defaultValue = "") String bookingId,
            @RequestParam(required = false, defaultValue = "") String totalAmount,
            @RequestParam(required = false, defaultValue = "") String vendorName,
            Model model, HttpSession session) {

        Payment newPayment = new Payment();
        if (!bookingId.isEmpty())   newPayment.setBookingId(bookingId);
        if (!vendorName.isEmpty())  newPayment.setVendorName(vendorName);
        if (!totalAmount.isEmpty()) newPayment.setTotalAmount(Double.parseDouble(totalAmount));

        // attach logged-in user
        String userId = (String) session.getAttribute("loggedInUser");
        if (userId != null) newPayment.setUserId(userId);

        model.addAttribute("newPayment", newPayment);
        return "payments/customer-payment";
    }

    // Client submits payment
    @PostMapping("/payments/add")
    public String addPayment(@ModelAttribute Payment payment,
                             HttpSession session,
                             RedirectAttributes ra) {
        // save userId from session
        String userId = (String) session.getAttribute("loggedInUser");
        if (userId != null) payment.setUserId(userId);

        paymentService.addPayment(payment);
        ra.addFlashAttribute("payment", payment);
        return "redirect:/payments/success";
    }

    // Success page
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
        model.addAttribute("payments", paymentService.getAllPayments());
        model.addAttribute("totalEarnings", paymentService.getTotalEarnings());
        return "admin/payments";
    }

    @GetMapping("/admin/payments/delete/{id}")
    public String adminDelete(@PathVariable String id,
                              HttpSession session, RedirectAttributes ra) {
        if (!isAdmin(session)) return "redirect:/admin/login";
        paymentService.deletePayment(id);
        ra.addFlashAttribute("message", "Payment deleted successfully.");
        return "redirect:/admin/payments";
    }

    @GetMapping("/admin/payments/edit/{id}")
    public String adminEditForm(@PathVariable String id,
                                Model model, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/admin/login";
        Payment payment = paymentService.getPaymentById(id);
        if (payment == null) return "redirect:/admin/payments";
        model.addAttribute("payment", payment);
        return "payments/payment-edit";
    }

    @PostMapping("/payments/update")
    public String updatePayment(@ModelAttribute Payment payment,
                                HttpSession session, RedirectAttributes ra) {
        if (!isAdmin(session)) return "redirect:/admin/login";
        paymentService.updatePayment(payment);
        ra.addFlashAttribute("message", "Payment updated successfully.");
        return "redirect:/admin/payments";
    }
}