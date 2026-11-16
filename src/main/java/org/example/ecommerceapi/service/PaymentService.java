package org.example.ecommerceapi.service;

import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import com.stripe.param.PaymentIntentCreateParams;
import jakarta.annotation.PostConstruct;
import org.example.ecommerceapi.dto.PaymentResponse;
import org.example.ecommerceapi.exception.*;
import org.example.ecommerceapi.model.Order;
import org.example.ecommerceapi.model.enums.OrderStatus;
import org.example.ecommerceapi.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Transactional
public class PaymentService {
    private final OrderRepository orderRepository;
    @Value("${stripe.api.key}")
    private String stripeSecretKey;
    @Value("${stripe.webhook.secret}")
    private String hookSecret;

    public PaymentService(OrderRepository orderRepository){
        this.orderRepository = orderRepository;
    }

    @PostConstruct
    public void init(){
        Stripe.apiKey = stripeSecretKey;
    }


    public PaymentResponse createPaymentIntent(Long orderId, String userEmail) {
        Order foundOrder = orderRepository.findById(orderId).orElseThrow(OrderNotFoundException::new);
        if (!foundOrder.getUser().getUsername().equals(userEmail)){
            throw new OrderNotFoundException();
        }
        if (foundOrder.getStatus().equals(OrderStatus.PAID)){
            throw new OrderAlreadyPaidException();
        }
        long totalPriceInCents = foundOrder.getTotalPrice().multiply(BigDecimal.valueOf(100)).longValue();

        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(totalPriceInCents)
                .setCurrency("EUR")
                .putMetadata("orderId", orderId.toString())
                .build();

        try {
            PaymentIntent intent = PaymentIntent.create(params);
            return new PaymentResponse(intent.getClientSecret());
        } catch (StripeException e) {
            throw new PaymentGatewayException(e);
        }
    }

    public void confirmPayment(String paymentIntentId){
        try {
            PaymentIntent intent = PaymentIntent.retrieve(paymentIntentId);
            if (!"succeeded".equals(intent.getStatus())){
                throw new PaymentNotCompletedException();
            }
            String orderIdStr = intent.getMetadata().get("orderId");
            Order order = orderRepository.findById(Long.parseLong(orderIdStr)).orElseThrow(OrderNotFoundException::new);
            order.setStatus(OrderStatus.PAID);

            orderRepository.save(order);
        } catch (StripeException e) {
            throw new PaymentGatewayException(e);
        }
    }

    public void handleWebHook(String payLoad, String sigHeader) {
        try {
            Event event = Webhook.constructEvent(payLoad,  sigHeader, hookSecret );

            if ("payment_intent.succeeded".equals(event.getType())){
                EventDataObjectDeserializer deserializer = event.getDataObjectDeserializer();
                if (deserializer.getObject().isPresent()){
                    PaymentIntent intent = (PaymentIntent) deserializer.getObject().get();

                    String orderIdStr = intent.getMetadata().get("orderId");

                    Order order = orderRepository.findById(Long.parseLong(orderIdStr)).orElseThrow(OrderNotFoundException::new);
                    order.setStatus(OrderStatus.PAID);
                    orderRepository.save(order);
                }
            }
        } catch (SignatureVerificationException e) {
            throw new InvalidWebhookSignatureException(e);
        }
    }


}
