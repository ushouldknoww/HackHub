package it.unicam.hackhub.utils.adapters;

import it.unicam.hackhub.model.dto.requestdto.PaymentResult;
import it.unicam.hackhub.model.enums.PayoutMethod;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Profile;
import java.util.UUID;

@Component
@Profile("local") // <-- Fondamentale
public class StripeAccountPayoutAdapter implements IPayoutAdapter {

    @Override
    public PayoutMethod supports() {
        return PayoutMethod.STRIPE_ACCOUNT;
    }

    @Override
    public PaymentResult transfer(double amount, String destination) {
        if (destination == null || !destination.startsWith("acct_")) {
            return PaymentResult.fail("L'ID dell'account Stripe deve iniziare con 'acct_'");
        }
        System.out.println("[SIMULAZIONE API STRIPE] Trasferimento di " + amount + "€ verso l'account connesso: " + destination);
        String providerRef = "tr_" + UUID.randomUUID().toString().replace("-", "").substring(0, 24);
        return PaymentResult.ok(providerRef);
    }
}