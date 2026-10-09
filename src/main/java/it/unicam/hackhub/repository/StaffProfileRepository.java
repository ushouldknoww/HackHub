package it.unicam.hackhub.repository;

import it.unicam.hackhub.model.StaffProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StaffProfileRepository extends JpaRepository<StaffProfile, Long> {

    @Query("SELECT s FROM StaffProfile s WHERE s.email.value = :email")
    StaffProfile findByEmail(@Param("email") String emailString);
}