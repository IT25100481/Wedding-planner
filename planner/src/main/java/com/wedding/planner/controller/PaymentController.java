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
    // Called from teammate's page:
    // /payments?bookingId=BK-001&vendorName=Pearl&totalAmount=25000
    @GetMapping("/payments")
    public String customerPayment(
            @RequestParam(required = false) String bookingId,
            @RequestParam(required = false) String totalAmount,
            @RequestParam(required = false) String vendorName,
            Model model) {

        Payment newPayment = new Payment();
        if (bookingId != null)    newPayment.setBookingId(bookingId);
        if (vendorName != null)   newPayment.setVendorName(vendorName);
        if (totalAmount != null)  newPayment.setTotalAmount(Double.parseDouble(totalAmount));

        model.addAttribute("newPayment", newPayment);
        return "payments/customer-payment";   // → templates/payments/customer-payment.html
    }

    // Client submits payment → save → redirect to success page
    @PostMapping("/payments/add")
    public String addPayment(@ModelAttribute Payment payment,
                             RedirectAttributes ra) {
        paymentService.addPayment(payment);   // sets ID, transactionId, status, createdAt
        // Pass payment to success page via flash (survives redirect)
        ra.addFlashAttribute("payment", payment);
        return "redirect:/payments/success";
    }

    // Success page
    @GetMapping("/payments/success")
    public String paymentSuccess(Model model) {
        // if someone navigates here directly without flash, just show a clean page
        if (!model.containsAttribute("payment")) {
            model.addAttribute("payment", new Payment());
        }
        return "payments/payment-success";    // → templates/payments/payment-success.html
    }

    // ════════════════════════════════════════════
    // ADMIN SIDE
    // ════════════════════════════════════════════

    // View all payments (admin)
    @GetMapping("/admin/payments")
    public String adminPayments(Model model, HttpSession session) {
        if (session.getAttribute("admin") == null) return "redirect:/admin/login";
        model.addAttribute("payments", paymentService.getAllPayments());
        model.addAttribute("totalEarnings", paymentService.getTotalEarnings());
        return "admin/payments";              // → templates/admin/payments.html
    }

    // Admin delete
    @GetMapping("/admin/payments/delete/{id}")
    public String adminDelete(@PathVariable String id,
                              HttpSession session,
                              RedirectAttributes ra) {
        if (session.getAttribute("admin") == null) return "redirect:/admin/login";
        paymentService.deletePayment(id);
        ra.addFlashAttribute("message", "Payment deleted successfully.");
        return "redirect:/admin/payments";
    }

    // Admin edit form
    @GetMapping("/admin/payments/edit/{id}")
    public String adminEditForm(@PathVariable String id,
                                Model model,
                                HttpSession session) {
        if (session.getAttribute("admin") == null) return "redirect:/admin/login";
        Payment payment = paymentService.getPaymentById(id);
        if (payment == null) return "redirect:/admin/payments";
        model.addAttribute("payment", payment);
        return "payments/payment-edit";       // → templates/payments/payment-edit.html
    }

    // Admin save edit
    @PostMapping("/payments/update")
    public String updatePayment(@ModelAttribute Payment payment,
                                HttpSession session,
                                RedirectAttributes ra) {
        if (session.getAttribute("admin") == null) return "redirect:/admin/login";
        paymentService.updatePayment(payment);
        ra.addFlashAttribute("message", "Payment updated successfully.");
        return "redirect:/admin/payments";
    }
}