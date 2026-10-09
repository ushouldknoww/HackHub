package it.unicam.hackhub.model;

import it.unicam.hackhub.model.valueobjs.Username;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private Username username;

    @Column(nullable = false, unique = true, length = 254)
    private String email;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String surname;

    @Column(name = "team_id")
    private Long team;

    // Il costruttore converte automaticamente la stringa nel Value Object per garantirti la validazione
    public User(String username, String email, String name, String surname) {
        this.username = new Username(username);
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("A valid email address is required");
        }
        this.email = email.trim().toLowerCase(java.util.Locale.ROOT);
        this.name = name;
        this.surname = surname;
    }

    public void assignTeam(Long teamId) {
        this.team = teamId;
    }
}