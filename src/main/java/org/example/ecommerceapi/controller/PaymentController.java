package org.example.ecommerceapi.controller;

import org.example.ecommerceapi.dto.PaymentConfirmRequest;
import org.example.ecommerceapi.dto.PaymentResponse;
import org.example.ecommerceapi.model.User;
import org.example.ecommerceapi.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/create-intent/{orderId}")
    public ResponseEntity<PaymentResponse> createIntent(@AuthenticationPrincipal User user, @PathVariable("orderId") Long orderId) {
        return ResponseEntity.ok(paymentService.createPaymentIntent(orderId, user.getUsername()));

    }

    @PostMapping("/confirm")
    public ResponseEntity<String> confirmPayment(@RequestBody PaymentConfirmRequest request) {
        paymentService.confirmPayment(request.paymentIntentId());
        return ResponseEntity.ok("Payment confirmed successfully");


    }

    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebHook(
            @RequestBody String payLoad,
            @RequestHeader("Stripe-Signature") String sigHeader
    ) {
        paymentService.handleWebHook(payLoad, sigHeader);
        return ResponseEntity.ok().build();

    }

}
