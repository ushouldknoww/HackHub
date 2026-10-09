package it.unicam.hackhub.model.valueobjs;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Column;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.regex.Pattern;

@Embeddable
@Getter
@EqualsAndHashCode
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Email {

    private static final String EMAIL_PATTERN = "^[^\\s@]+@[^\\s@]+\\.[^\\s@]{2,}$";
    private static final Pattern PATTERN = Pattern.compile(EMAIL_PATTERN);

    @Column(name = "email", nullable = false)
    private String value;

    public Email(String value) {
        if (value == null || value.trim().isBlank()) {
            throw new IllegalArgumentException("L'indirizzo email non può essere vuoto.");
        }

        String cleanEmail = value.trim().toLowerCase();

        if (cleanEmail.length() > 254 || !PATTERN.matcher(cleanEmail).matches()) {
            throw new IllegalArgumentException("Formato email non valido: " + value);
        }

        this.value = cleanEmail;
    }
}