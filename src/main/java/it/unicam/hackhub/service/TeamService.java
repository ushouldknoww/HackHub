package it.unicam.hackhub.service;

import it.unicam.hackhub.model.Team;
import it.unicam.hackhub.model.User;
import it.unicam.hackhub.model.dto.requestdto.CreateTeamDTO;
import it.unicam.hackhub.repository.TeamRepository;
import it.unicam.hackhub.repository.UserRepository;
import it.unicam.hackhub.utils.DomainException;
import it.unicam.hackhub.validators.TeamValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TeamService {

    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final TeamValidator teamValidator;

    public TeamService(TeamRepository teamRepository, UserRepository userRepository, TeamValidator teamValidator) {
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
        this.teamValidator = teamValidator;
    }

    @Transactional
    public void createTeam(Long userId, CreateTeamDTO createTeamDTO) {
        teamValidator.validate(createTeamDTO);

        if (teamRepository.existsByName(createTeamDTO.getName())) {
            throw new DomainException("Esiste già un team con questo nome");
        }

        if (teamRepository.existsByMemberId(userId)) {
            throw new DomainException("L'utente fa già parte di un team");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DomainException("Utente non trovato"));

        Team createdTeam = new Team(createTeamDTO.getName(), userId);
        teamRepository.save(createdTeam);

        user.assignTeam(createdTeam.getId());
        userRepository.save(user);
    }
}