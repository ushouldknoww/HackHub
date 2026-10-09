package it.unicam.hackhub.model.valueobjs;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Password {

    private static final int MIN_LENGTH = 8;

    @Column(name = "password", nullable = false)
    private String value;

    public Password(String candidate) {
        if (candidate == null || candidate.trim().isBlank()) {
            throw new IllegalArgumentException("La password non può essere vuota");
        }

        if (candidate.length() < MIN_LENGTH) {
            throw new IllegalArgumentException("La password deve essere lunga almeno " + MIN_LENGTH + " caratteri");
        }

        this.value = candidate;
    }

    @Override
    public String toString() {
        return "********";
    }
}