package it.unicam.hackhub.repository;

import it.unicam.hackhub.model.ParticipatingTeam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParticipatingTeamRepository extends JpaRepository<ParticipatingTeam, Long> {

    @Query("SELECT COUNT(pt) > 0 FROM ParticipatingTeam pt WHERE pt.hackathon = :hackathonId AND pt.team = :teamId")
    boolean existsByHackathonIdAndTeamId(@Param("hackathonId") Long hackathonId, @Param("teamId") Long teamId);

    @Query("SELECT pt FROM ParticipatingTeam pt WHERE pt.hackathon = :hackathonId AND pt.team = :teamId")
    ParticipatingTeam findByHackathonIdAndTeamId(@Param("hackathonId") Long hackathonId, @Param("teamId") Long teamId);

    @Query("SELECT pt FROM ParticipatingTeam pt WHERE pt.id = :id AND pt.hackathon = :hackathonId")
    ParticipatingTeam getByIdAndHackathonId(@Param("id") Long id, @Param("hackathonId") Long hackathonId);

    @Query("SELECT pt FROM ParticipatingTeam pt WHERE pt.hackathon = :hackathonId AND :userId MEMBER OF pt.activeMembers")
    ParticipatingTeam findByHackathonIdAndActiveMemberId(@Param("hackathonId") Long hackathonId, @Param("userId") Long userId);

    @Query("SELECT pt FROM ParticipatingTeam pt WHERE pt.hackathon = :hackathonId AND pt.disqualified = false")
    List<ParticipatingTeam> findEligibleForRanking(@Param("hackathonId") Long hackathonId);
}