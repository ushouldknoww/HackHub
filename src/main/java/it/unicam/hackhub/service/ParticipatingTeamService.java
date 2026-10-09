package it.unicam.hackhub.service;

import it.unicam.hackhub.model.ParticipatingTeam;
import it.unicam.hackhub.model.Team;
import it.unicam.hackhub.model.dto.requestdto.RegisterTeamDTO;
import it.unicam.hackhub.model.enums.HackathonStatus;
import it.unicam.hackhub.repository.HackathonRepository;
import it.unicam.hackhub.repository.ParticipatingTeamRepository;
import it.unicam.hackhub.repository.TeamRepository;
import it.unicam.hackhub.utils.DomainException;
import it.unicam.hackhub.validators.ParticipatingTeamValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ParticipatingTeamService {

    private final TeamRepository teamRepository;
    private final HackathonRepository hackathonRepository;
    private final ParticipatingTeamRepository participatingTeamRepository;
    private final ParticipatingTeamValidator validator;

    public ParticipatingTeamService(TeamRepository teamRepository, 
                                    HackathonRepository hackathonRepository, 
                                    ParticipatingTeamRepository participatingTeamRepository, 
                                    ParticipatingTeamValidator validator) {
        this.teamRepository = teamRepository;
        this.hackathonRepository = hackathonRepository;
        this.participatingTeamRepository = participatingTeamRepository;
        this.validator = validator;
    }

    @Transactional
    public void registerTeamToHackathon(Long userId, Long hackathonId, RegisterTeamDTO registerTeamDTO) {
        validator.validate(registerTeamDTO);

        Team team = teamRepository.findByLeaderId(userId);
        if (team == null) {
            throw new DomainException("Utente non autorizzato: non sei il leader di alcun team");
        }

        HackathonStatus hackathonStatus = hackathonRepository.findStatusByHackathonId(hackathonId);
        if (hackathonStatus != HackathonStatus.IN_REGISTRATION) {
            throw new DomainException("Le iscrizioni per questo hackathon non sono aperte");
        }

        if (participatingTeamRepository.existsByHackathonIdAndTeamId(hackathonId, team.getId())) {
            throw new DomainException("Il team è già iscritto a questo hackathon");
        }

        int maxTeamSize = hackathonRepository.findMaxTeamSizeByHackathonId(hackathonId);
        int teamSize = team.getTeamSize();

        if (teamSize > maxTeamSize || teamSize < 1) {
            throw new DomainException("La dimensione del team non rispetta i limiti dell'hackathon");
        }

        List<Long> membersSnapshot = team.getMemberIdsSnapshot();

        ParticipatingTeam participatingTeam = new ParticipatingTeam(
                hackathonId,
                team.getId(),
                membersSnapshot,
                registerTeamDTO.getContactEmail(),
                registerTeamDTO.getPayoutMethod(),
                registerTeamDTO.getPayoutRef(),
                LocalDateTime.now()
        );

        participatingTeamRepository.save(participatingTeam);
    }
}