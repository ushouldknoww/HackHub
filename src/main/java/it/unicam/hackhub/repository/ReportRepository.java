package it.unicam.hackhub.repository;

import it.unicam.hackhub.model.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

    @Query("SELECT r FROM Report r WHERE r.hackathon = :hackathonId")
    List<Report> getByHackathonId(@Param("hackathonId") Long hackathonId);

    @Query("SELECT r FROM Report r WHERE r.id = :id AND r.hackathon = :hackathonId")
    Report getByIdAndHackathonId(@Param("id") Long id, @Param("hackathonId") Long hackathonId);
}