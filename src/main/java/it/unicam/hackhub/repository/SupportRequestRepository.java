package it.unicam.hackhub.repository;

import it.unicam.hackhub.model.SupportRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupportRequestRepository extends JpaRepository<SupportRequest, Long> {

    @Query("SELECT s FROM SupportRequest s WHERE s.hackathon = :hackathonId")
    List<SupportRequest> getByHackathonId(@Param("hackathonId") Long hackathonId);

    @Query("SELECT s FROM SupportRequest s WHERE s.id = :id AND s.hackathon = :hackathonId")
    SupportRequest getByIdAndHackathonId(@Param("id") Long id, @Param("hackathonId") Long hackathonId);
}