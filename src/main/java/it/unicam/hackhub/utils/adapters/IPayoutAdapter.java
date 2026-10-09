package it.unicam.hackhub.utils.adapters;

import it.unicam.hackhub.model.dto.requestdto.PaymentResult;
import it.unicam.hackhub.model.enums.PayoutMethod;

public interface IPayoutAdapter {
    PayoutMethod supports();
    PaymentResult transfer(double amount, String destination);
}