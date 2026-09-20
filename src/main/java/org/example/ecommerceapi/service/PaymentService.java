package org.example.ecommerceapi.service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import jakarta.annotation.PostConstruct;
import org.example.ecommerceapi.dto.PaymentResponse;
import org.example.ecommerceapi.model.Order;
import org.example.ecommerceapi.model.enums.OrderStatus;
import org.example.ecommerceapi.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class PaymentService {
    private final OrderRepository orderRepository;
    @Value("${stripe.api.key}")
    private String stripeSecretKey;

    public PaymentService(OrderRepository orderRepository){
        this.orderRepository = orderRepository;
    }

    @PostConstruct
    public void init(){
        Stripe.apiKey = stripeSecretKey;
    }


    public PaymentResponse createPaymentIntent(Long orderId, String userEmail) throws StripeException {
        Order foundOrder = orderRepository.findById(orderId).orElseThrow(() -> new IllegalArgumentException("Order wasn't found!"));
        if (!foundOrder.getUser().getUsername().equals(userEmail)){
            throw new IllegalArgumentException("Illegal Username for this order!");
        }
        if (foundOrder.getStatus().equals(OrderStatus.PAID)){
            throw new IllegalStateException("Order was already paid!");
        }
        long totalPriceInCents = foundOrder.getTotalPrice().multiply(BigDecimal.valueOf(100)).longValue();

        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(totalPriceInCents)
                .setCurrency("EUR")
                .build();

        PaymentIntent intent = PaymentIntent.create(params);

        return new PaymentResponse(intent.getClientSecret());
    }


}
