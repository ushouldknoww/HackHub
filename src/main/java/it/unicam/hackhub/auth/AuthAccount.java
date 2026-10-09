package it.unicam.hackhub.auth;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "auth_accounts", indexes = {
        @Index(name = "idx_auth_account_subject", columnList = "account_role, subject_id")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AuthAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 254)
    private String username;

    @Column(name = "password_hash", nullable = false, length = 100)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "account_role", nullable = false, length = 20)
    private AccountRole role;

    /** ID of the corresponding User or StaffProfile domain entity. */
    @Column(name = "subject_id", nullable = false)
    private Long subjectId;

    public AuthAccount(String username, String passwordHash, AccountRole role, Long subjectId) {
        this.username = username == null ? null : username.trim().toLowerCase();
        this.passwordHash = passwordHash;
        this.role = role;
        this.subjectId = subjectId;
    }
}
