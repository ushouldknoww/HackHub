package it.unicam.hackhub.model.valueobjs;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Column;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Embeddable
@Getter
@EqualsAndHashCode
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Username {

    @Column(name = "username", nullable = false, unique = true)
    private String value;

    public Username(String value) {
        if (value == null || value.trim().length() < 3) {
            throw new IllegalArgumentException("L'username deve contenere almeno 3 caratteri");
        }
        this.value = value.trim();
    }
}