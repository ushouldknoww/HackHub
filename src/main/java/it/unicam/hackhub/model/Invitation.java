package it.unicam.hackhub.model;

import it.unicam.hackhub.model.enums.InvitationStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "invitations")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Invitation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "team_id", nullable = false)
    private Long team;

    @Column(name = "invitee_id", nullable = false)
    private Long invitee;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private InvitationStatus status;

    public Invitation(Long teamId, Long inviteeId) {
        this.team = teamId;
        this.invitee = inviteeId;
        this.status = InvitationStatus.PENDING;
    }

    public Long getTeamId() {
        return this.team;
    }

    public void accept() {
        if (this.status == InvitationStatus.PENDING) {
            this.status = InvitationStatus.ACCEPTED;
        } else {
            throw new IllegalStateException("Impossibile accettare: l'invito non è in attesa.");
        }
    }

    public void reject() {
        if (this.status == InvitationStatus.PENDING) {
            this.status = InvitationStatus.REJECTED;
        } else {
            throw new IllegalStateException("Impossibile rifiutare: l'invito non è in attesa.");
        }
    }
}