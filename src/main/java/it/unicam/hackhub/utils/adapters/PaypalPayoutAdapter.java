package it.unicam.hackhub.utils.adapters;

import it.unicam.hackhub.model.dto.requestdto.PaymentResult;
import it.unicam.hackhub.model.enums.PayoutMethod;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Profile;
import java.util.UUID;

@Component
@Profile("local") // <-- Fondamentale
public class PaypalPayoutAdapter implements IPayoutAdapter {

    @Override
    public PayoutMethod supports() {
        return PayoutMethod.PAYPAL;
    }

    @Override
    public PaymentResult transfer(double amount, String destination) {
        if (destination == null || !destination.contains("@")) {
            return PaymentResult.fail("Indirizzo email PayPal non valido");
        }
        System.out.println("[SIMULAZIONE API PAYPAL] Invio fondi di " + amount + "€ all'account: " + destination);
        String providerRef = "PAYPAL-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase();
        return PaymentResult.ok(providerRef);
    }
}