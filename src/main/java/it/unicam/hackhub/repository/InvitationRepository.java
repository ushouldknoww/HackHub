package it.unicam.hackhub.repository;

import it.unicam.hackhub.model.Invitation;
import it.unicam.hackhub.model.enums.InvitationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvitationRepository extends JpaRepository<Invitation, Long> {

    @Query("SELECT COUNT(i) > 0 FROM Invitation i WHERE i.team = :teamId AND i.invitee = :inviteeId AND i.status = :status")
    boolean existsPendingByTeamIdAndInviteeId(@Param("teamId") Long teamId,
                                              @Param("inviteeId") Long inviteeId,
                                              @Param("status") InvitationStatus status);

    @Query("SELECT i FROM Invitation i WHERE i.id = :id AND i.invitee = :inviteeId AND i.status = :status")
    Invitation findByIdAndInviteeIdAndStatus(@Param("id") Long id, @Param("inviteeId") Long inviteeId, @Param("status") InvitationStatus status);

    @Query("SELECT i FROM Invitation i WHERE i.invitee = :userId")
    List<Invitation> findByInviteeId(@Param("userId") Long userId);

    @Query("SELECT i FROM Invitation i WHERE i.id = :id AND i.invitee = :inviteeId")
    Invitation getByIdAndInviteeId(@Param("id") Long id, @Param("inviteeId") Long inviteeId);
}