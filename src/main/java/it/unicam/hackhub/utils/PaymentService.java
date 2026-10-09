package it.unicam.hackhub.utils;

import it.unicam.hackhub.utils.adapters.IPayoutAdapter;
import it.unicam.hackhub.model.dto.requestdto.PaymentResult;
import it.unicam.hackhub.model.valueobjs.PayoutAccountRef;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentService implements IPaymentService {

    private final List<IPayoutAdapter> adapters;

    // Spring inietta qui automaticamente tutti gli IPayoutAdapter trovati nel progetto!
    public PaymentService(List<IPayoutAdapter> adapters) {
        this.adapters = adapters;
    }

    @Override
    public PaymentResult transfer(double amount, PayoutAccountRef destination) {

        if (!Double.isFinite(amount) || amount <= 0) {
            return PaymentResult.fail("L'importo del pagamento deve essere finito e maggiore di zero");
        }
        if (destination == null || destination.getMethod() == null || destination.getValue() == null) {
            return PaymentResult.fail("Dati di destinazione del pagamento mancanti o incompleti");
        }

        for (IPayoutAdapter adapter : adapters) {
            if (adapter.supports() == destination.getMethod()) {
                return adapter.transfer(amount, destination.getValue());
            }
        }

        return PaymentResult.fail("Nessun servizio di pagamento configurato per il metodo: " + destination.getMethod());
    }
}