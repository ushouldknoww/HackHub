package it.unicam.hackhub.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AuthAccountRepository extends JpaRepository<AuthAccount, Long> {
    Optional<AuthAccount> findByUsernameIgnoreCase(String username);
    boolean existsByUsernameIgnoreCase(String username);
}
