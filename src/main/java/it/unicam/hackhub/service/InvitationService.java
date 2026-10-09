package it.unicam.hackhub.service;

import it.unicam.hackhub.model.Invitation;
import it.unicam.hackhub.model.Team;
import it.unicam.hackhub.model.User;
import it.unicam.hackhub.model.enums.InvitationStatus;
import it.unicam.hackhub.repository.InvitationRepository;
import it.unicam.hackhub.repository.TeamRepository;
import it.unicam.hackhub.repository.UserRepository;
import it.unicam.hackhub.utils.DomainException;
import it.unicam.hackhub.notifications.InvitationCreatedEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class InvitationService {

    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final InvitationRepository invitationRepository;
    private final ApplicationEventPublisher eventPublisher;

    public InvitationService(UserRepository userRepository, TeamRepository teamRepository,
                             InvitationRepository invitationRepository, ApplicationEventPublisher eventPublisher) {
        this.userRepository = userRepository;
        this.teamRepository = teamRepository;
        this.invitationRepository = invitationRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional(readOnly = true)
    public User searchUser(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new DomainException("Utente non trovato");
        }
        return user;
    }

    @Transactional
    public Invitation inviteUser(Long inviterUserId, Long inviteeUserId) {
        Team inviterTeam = teamRepository.findByLeaderId(inviterUserId);
        if (inviterTeam == null) {
            throw new DomainException("Operazione negata: non sei il leader di alcun team");
        }

        Team inviteeTeam = teamRepository.findByMemberId(inviteeUserId);
        if (inviteeTeam != null) {
            throw new DomainException("L'utente fa già parte di un team");
        }

        if (invitationRepository.existsPendingByTeamIdAndInviteeId(inviterTeam.getId(), inviteeUserId, InvitationStatus.PENDING)) {
            throw new DomainException("Esiste già un invito in attesa di risposta per questo utente");
        }

        User inviter = userRepository.findById(inviterUserId)
                .orElseThrow(() -> new DomainException("Utente invitante non trovato"));
        User invitee = userRepository.findById(inviteeUserId)
                .orElseThrow(() -> new DomainException("Utente invitato non trovato"));

        Invitation createdInvitation = invitationRepository.save(new Invitation(inviterTeam.getId(), inviteeUserId));
        eventPublisher.publishEvent(new InvitationCreatedEvent(invitee.getEmail(), invitee.getName(),
                inviter.getName(), inviterTeam.getName(), createdInvitation.getId()));
        return createdInvitation;
    }

    @Transactional(readOnly = true)
    public List<Invitation> getInvitationsList(Long userId) {
        return invitationRepository.findByInviteeId(userId);
    }

    @Transactional(readOnly = true)
    public Invitation getInvitationDetails(Long userId, Long invitationId) {
        return invitationRepository.getByIdAndInviteeId(invitationId, userId);
    }

    @Transactional
    public void acceptInvitation(Long userId, Long invitationId) {
        Invitation invitation = invitationRepository.findByIdAndInviteeIdAndStatus(invitationId, userId, InvitationStatus.PENDING);
        if (invitation == null) {
            throw new DomainException("Non è stato trovato l'invito selezionato per questo utente");
        }

        if (teamRepository.existsByMemberId(userId)) {
            throw new DomainException("L'utente appartiene già a un team");
        }

        User user = userRepository.findById(invitation.getInvitee())
                .orElseThrow(() -> new DomainException("Utente non trovato"));
                
        Team team = teamRepository.findById(invitation.getTeamId())
                .orElseThrow(() -> new DomainException("Team non trovato"));

        team.addMember(userId);
        user.assignTeam(team.getId());
        invitation.accept();

        invitationRepository.save(invitation);
        teamRepository.save(team);
        userRepository.save(user);
    }

    @Transactional
    public void rejectInvitation(Long userId, Long invitationId) {
        Invitation invitation = invitationRepository.findByIdAndInviteeIdAndStatus(invitationId, userId, InvitationStatus.PENDING);
        if (invitation == null) {
            throw new DomainException("Non è stato trovato l'invito selezionato per questo utente");
        }

        invitation.reject();
        invitationRepository.save(invitation);
    }
}