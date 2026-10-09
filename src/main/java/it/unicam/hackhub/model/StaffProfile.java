package it.unicam.hackhub.model;

import it.unicam.hackhub.model.valueobjs.Email;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "staff_profiles")
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StaffProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "email", nullable = false, unique = true, length = 255))
    private Email email;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String surname;

    public StaffProfile(Email email, String name, String surname) {
        this.email = email;
        this.name = name;
        this.surname = surname;
    }
}