package it.unicam.hackhub.repository;

import it.unicam.hackhub.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Navighiamo dentro l'Embeddable 'username' per cercare la stringa 'value'
    @Query("SELECT u FROM User u WHERE u.username.value = :username")
    User findByUsername(@Param("username") String usernameString);

    boolean existsByEmailIgnoreCase(String email);
    
}