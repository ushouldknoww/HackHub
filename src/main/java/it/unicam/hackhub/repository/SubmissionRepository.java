package it.unicam.hackhub.repository;

import it.unicam.hackhub.model.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {

    @Query("SELECT s FROM Submission s WHERE s.participatingTeam = :participatingTeamId")
    Submission findByParticipatingTeamId(@Param("participatingTeamId") Long participatingTeamId);

    @Query("SELECT COUNT(s) > 0 FROM Submission s WHERE s.participatingTeam = :participatingTeamId")
    boolean existsByParticipatingTeamId(@Param("participatingTeamId") Long participatingTeamId);

    @Query("SELECT s FROM Submission s WHERE s.hackathon = :hackathonId")
    List<Submission> findByHackathonId(@Param("hackathonId") Long hackathonId);

    @Query("SELECT s FROM Submission s WHERE s.id = :id AND s.hackathon = :hackathonId")
    Submission getByIdAndHackathonId(@Param("id") Long id, @Param("hackathonId") Long hackathonId);

    @Query("SELECT COUNT(s) > 0 FROM Submission s WHERE s.hackathon = :hackathonId AND s.evaluation IS NULL")
    boolean existsByHackathonIdAndEvaluationIsNull(@Param("hackathonId") Long hackathonId);

    @Query("SELECT s FROM Submission s WHERE s.hackathon = :hackathonId AND s.participatingTeam = :participatingTeamId")
    Submission findByHackathonIdAndParticipatingTeamId(@Param("hackathonId") Long hackathonId, @Param("participatingTeamId") Long participatingTeamId);
}