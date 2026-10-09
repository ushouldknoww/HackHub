package it.unicam.hackhub.service;

import it.unicam.hackhub.model.Hackathon;
import it.unicam.hackhub.model.Submission;
import it.unicam.hackhub.model.dto.requestdto.AddEvaluationDTO;
import it.unicam.hackhub.model.enums.HackathonStatus;
import it.unicam.hackhub.repository.HackathonRepository;
import it.unicam.hackhub.repository.SubmissionRepository;
import it.unicam.hackhub.validators.EvaluationValidator;
import it.unicam.hackhub.utils.DomainException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EvaluationService {

    private final HackathonRepository hackathonRepository;
    private final SubmissionRepository submissionRepository;
    private final EvaluationValidator evaluationValidator;

    public EvaluationService(HackathonRepository hackathonRepository, 
                             SubmissionRepository submissionRepository, 
                             EvaluationValidator evaluationValidator) {
        this.hackathonRepository = hackathonRepository;
        this.submissionRepository = submissionRepository;
        this.evaluationValidator = evaluationValidator;
    }

    @Transactional
    public void addEvaluation(Long staffProfileId, Long hackathonId, Long submissionId, AddEvaluationDTO dto) {
        evaluationValidator.validate(dto, staffProfileId, submissionId);

        if (!hackathonRepository.existsJudge(hackathonId, staffProfileId)) {
            throw new DomainException("Operazione non autorizzata: l'utente non è il giudice di questo hackathon");
        }

        HackathonStatus hackathonStatus = hackathonRepository.findStatusByHackathonId(hackathonId);
        if (hackathonStatus != HackathonStatus.IN_EVALUATION) {
            throw new DomainException("L'hackathon non è attualmente in fase di valutazione");
        }

        Submission submission = submissionRepository.getByIdAndHackathonId(submissionId, hackathonId);
        if (submission == null) {
            throw new DomainException("Sottomissione non trovata per questo hackathon");
        }

        if (submission.hasEvaluation()) {
            throw new DomainException("La sottomissione è già stata valutata");
        }

        submission.addEvaluation(dto.getScore(), dto.getComment());
        submissionRepository.save(submission);
    }

    @Transactional
    public void confirmEvaluations(Long staffProfileId, Long hackathonId) {
        if (!hackathonRepository.existsJudge(hackathonId, staffProfileId)) {
            throw new DomainException("Operazione non autorizzata: solo il giudice può confermare le valutazioni.");
        }

        HackathonStatus hackathonStatus = hackathonRepository.findStatusByHackathonId(hackathonId);
        if (hackathonStatus != HackathonStatus.IN_EVALUATION) {
            throw new DomainException("Impossibile confermare le valutazioni: l'hackathon non è attualmente in fase di valutazione.");
        }

        if (submissionRepository.existsByHackathonIdAndEvaluationIsNull(hackathonId)) {
            throw new DomainException("Impossibile confermare: ci sono ancora sottomissioni senza valutazione per questo hackathon.");
        }

        Hackathon hackathon = hackathonRepository.findById(hackathonId)
                .orElseThrow(() -> new DomainException("Hackathon non trovato."));

        hackathon.close();
        hackathonRepository.save(hackathon);
    }
}