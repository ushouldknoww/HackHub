package it.unicam.hackhub.model.dto.requestdto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PaymentResult {

    private boolean success;
    private String providerRef;
    private String failureReason;

    public static PaymentResult ok(String providerRef) {
        return new PaymentResult(true, providerRef, null);
    }

    public static PaymentResult fail(String reason) {
        return new PaymentResult(false, null, reason);
    }

    public String getTransactionId() {
        return this.providerRef;
    }

    public String getErrorMessage() {
        return this.failureReason;
    }
}