package it.unicam.hackhub.utils;

import it.unicam.hackhub.model.dto.requestdto.PaymentResult;
import it.unicam.hackhub.model.valueobjs.PayoutAccountRef;

public interface IPaymentService {

    PaymentResult transfer(double amount, PayoutAccountRef destination);

}