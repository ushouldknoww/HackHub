package it.unicam.hackhub.service;

import it.unicam.hackhub.model.Hackathon;
import it.unicam.hackhub.model.ParticipatingTeam;
import it.unicam.hackhub.model.Submission;
import it.unicam.hackhub.model.dto.requestdto.AddSubmissionDTO;
import it.unicam.hackhub.model.enums.HackathonStatus;
import it.unicam.hackhub.repository.HackathonRepository;
import it.unicam.hackhub.repository.ParticipatingTeamRepository;
import it.unicam.hackhub.repository.SubmissionRepository;
import it.unicam.hackhub.utils.DomainException;
import it.unicam.hackhub.validators.SubmissionValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SubmissionService {

    private final SubmissionValidator submissionValidator;
    private final SubmissionRepository submissionRepository;
    private final HackathonRepository hackathonRepository;
    private final ParticipatingTeamRepository participatingTeamRepository;

    public SubmissionService(SubmissionValidator submissionValidator, 
                             SubmissionRepository submissionRepository, 
                             HackathonRepository hackathonRepository, 
                             ParticipatingTeamRepository participatingTeamRepository) {
        this.submissionValidator = submissionValidator;
        this.submissionRepository = submissionRepository;
        this.hackathonRepository = hackathonRepository;
        this.participatingTeamRepository = participatingTeamRepository;
    }

    @Transactional
    public void createSubmission(Long userId, Long hackathonId, AddSubmissionDTO addSubmissionDTO) {
        submissionValidator.validate(addSubmissionDTO);

        HackathonStatus hackathonStatus = hackathonRepository.findStatusByHackathonId(hackathonId);
        if (hackathonStatus != HackathonStatus.RUNNING) {
            throw new DomainException("L'hackathon non è in corso");
        }

        ParticipatingTeam participatingTeam = participatingTeamRepository.findByHackathonIdAndActiveMemberId(hackathonId, userId);
        if (participatingTeam == null) {
            throw new DomainException("Utente non autorizzato o team non trovato");
        }

        if (submissionRepository.existsByParticipatingTeamId(participatingTeam.getId())) {
            throw new DomainException("Sottomissione già esistente per questo team");
        }

        if (participatingTeam.isDisqualified()) {
            throw new DomainException("Il team è stato squalificato");
        }

        Submission createdSubmission = new Submission(
                hackathonId,
                participatingTeam.getId(),
                addSubmissionDTO.getResponse(),
                addSubmissionDTO.getResponseURL()
        );

        submissionRepository.save(createdSubmission);
    }

    @Transactional(readOnly = true)
    public List<Submission> getSubmissionsList(Long staffProfileId, Long hackathonId) {
        if (!hackathonRepository.existsStaff(hackathonId, staffProfileId)) {
            throw new DomainException("Operazione non autorizzata: non fai parte dello staff");
        }
        return submissionRepository.findByHackathonId(hackathonId);
    }

    @Transactional(readOnly = true)
    public Submission getSubmissionDetails(Long staffProfileId, Long hackathonId, Long submissionId) {
        if (!hackathonRepository.existsStaff(hackathonId, staffProfileId)) {
            throw new DomainException("Operazione non autorizzata: non fai parte dello staff");
        }

        if (!hackathonRepository.existsById(hackathonId)) {
            throw new DomainException("Hackathon non trovato");
        }

        Submission submission = submissionRepository.getByIdAndHackathonId(submissionId, hackathonId);
        if (submission == null) {
            throw new DomainException("La sottomissione non appartiene all'hackathon selezionato");
        }

        return submission;
    }
}