package it.unicam.hackhub.repository;

import it.unicam.hackhub.model.Hackathon;
import it.unicam.hackhub.model.enums.HackathonStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface HackathonRepository extends JpaRepository<Hackathon, Long>, JpaSpecificationExecutor<Hackathon> {

    boolean existsByName(String name);

    /** Locks a hackathon while settling the prize, preventing concurrent payout attempts. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT h FROM Hackathon h WHERE h.id = :id")
    Optional<Hackathon> findByIdForUpdate(@Param("id") Long id);

    @Query("SELECT h.status FROM Hackathon h WHERE h.id = :id")
    HackathonStatus findStatusByHackathonId(@Param("id") Long id);

    @Query("SELECT h.maxTeamSize FROM Hackathon h WHERE h.id = :id")
    Integer findMaxTeamSizeByHackathonId(@Param("id") Long id);

    @Query("SELECT CASE WHEN COUNT(h) > 0 THEN true ELSE false END FROM Hackathon h WHERE h.id = :hackathonId AND h.organizer = :staffId")
    boolean existsOrganizer(@Param("hackathonId") Long hackathonId, @Param("staffId") Long staffId);

    @Query("SELECT CASE WHEN COUNT(h) > 0 THEN true ELSE false END FROM Hackathon h WHERE h.id = :hackathonId AND :staffId MEMBER OF h.mentors")
    boolean existsMentor(@Param("hackathonId") Long hackathonId, @Param("staffId") Long staffId);

    @Query("SELECT CASE WHEN COUNT(h) > 0 THEN true ELSE false END FROM Hackathon h WHERE h.id = :hackathonId AND h.judge = :staffId")
    boolean existsJudge(@Param("hackathonId") Long hackathonId, @Param("staffId") Long staffId);

    @Query("SELECT CASE WHEN COUNT(h) > 0 THEN true ELSE false END FROM Hackathon h " +
           "WHERE h.id = :hackathonId AND (h.organizer = :staffId OR h.judge = :staffId OR :staffId MEMBER OF h.mentors)")
    boolean existsStaff(@Param("hackathonId") Long hackathonId, @Param("staffId") Long staffId);
}