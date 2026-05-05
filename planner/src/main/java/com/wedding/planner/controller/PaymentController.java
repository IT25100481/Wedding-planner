package com.wedding.planner.controller;

import com.wedding.planner.model.Payment;
import com.wedding.planner.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    // ── READ - Show all payments ──
    @GetMapping
    public String listPayments(Model model) {
        model.addAttribute("payments", paymentService.getAllPayments());
        model.addAttribute("totalEarnings", paymentService.getTotalEarnings());
        model.addAttribute("newPayment", new Payment());
        return "payments";
    }

    // ── CREATE - Add new payment ──
    @PostMapping("/add")
    public String addPayment(@ModelAttribute Payment payment) {
        paymentService.addPayment(payment);
        return "redirect:/payments?success=added";
    }

    // ── READ - Show edit form ──
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable String id, Model model) {
        Payment payment = paymentService.getPaymentById(id);
        if (payment == null) return "redirect:/payments";
        model.addAttribute("payment", payment);
        return "payment-edit";
    }

    // ── UPDATE - Save edited payment ──
    @PostMapping("/update")
    public String updatePayment(@ModelAttribute Payment payment) {
        paymentService.updatePayment(payment);
        return "redirect:/payments?success=updated";
    }

    // ── DELETE - Remove payment ──
    @GetMapping("/delete/{id}")
    public String deletePayment(@PathVariable String id) {
        paymentService.deletePayment(id);
        return "redirect:/payments?success=deleted";
    }
}