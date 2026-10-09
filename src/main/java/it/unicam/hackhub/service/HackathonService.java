package it.unicam.hackhub.service;

import it.unicam.hackhub.model.*;
import it.unicam.hackhub.model.dto.requestdto.CreateHackathonDTO;
import it.unicam.hackhub.model.dto.requestdto.HackathonSearchCriteria;
import it.unicam.hackhub.model.dto.requestdto.PaymentResult;
import it.unicam.hackhub.model.dto.response.LeaderboardEntry;
import it.unicam.hackhub.model.dto.response.PublicHackathonView;
import it.unicam.hackhub.model.enums.HackathonStatus;
import it.unicam.hackhub.model.enums.PrizeStatus;
import it.unicam.hackhub.model.valueobjs.PayoutAccountRef;
import it.unicam.hackhub.repository.*;
import java.util.Comparator;
import it.unicam.hackhub.utils.DomainException;
import it.unicam.hackhub.utils.IPaymentService;
import it.unicam.hackhub.utils.WinnerService;
import it.unicam.hackhub.utils.builders.HackathonBuilder;
import it.unicam.hackhub.utils.builders.IHackathonBuilder;
import it.unicam.hackhub.validators.HackathonValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class HackathonService {

    private final StaffProfileRepository staffProfileRepository;
    private final HackathonRepository hackathonRepository;
    private final SubmissionRepository submissionRepository;
    private final ParticipatingTeamRepository participatingTeamRepository;
    private final TeamRepository teamRepository;
    private final HackathonValidator hackathonValidator;
    private final WinnerService winnerService;
    private final IPaymentService paymentService;

    public HackathonService(StaffProfileRepository staffProfileRepository,
                            HackathonRepository hackathonRepository,
                            SubmissionRepository submissionRepository,
                            ParticipatingTeamRepository participatingTeamRepository,
                            TeamRepository teamRepository,
                            HackathonValidator hackathonValidator,
                            WinnerService winnerService,
                            IPaymentService paymentService) {
        this.staffProfileRepository = staffProfileRepository;
        this.hackathonRepository = hackathonRepository;
        this.submissionRepository = submissionRepository;
        this.participatingTeamRepository = participatingTeamRepository;
        this.teamRepository = teamRepository;
        this.hackathonValidator = hackathonValidator;
        this.winnerService = winnerService;
        this.paymentService = paymentService;
    }

    @Transactional
    public void createHackathon(Long staffProfileId, CreateHackathonDTO createHackathonDTO) {
        hackathonValidator.validate(createHackathonDTO);

        StaffProfile organizer = staffProfileRepository.findById(staffProfileId)
                .orElseThrow(() -> new DomainException("Organizzatore non trovato"));

        if (hackathonRepository.existsByName(createHackathonDTO.getName())) {
            throw new DomainException("Esiste già un hackathon con questo nome");
        }

        StaffProfile judge = staffProfileRepository.findByEmail(createHackathonDTO.getJudgeEmail());
        if (judge == null) {
            throw new DomainException("Nessun profilo staff trovato per l'email del giudice");
        }

        List<Long> mentorsId = new ArrayList<>();
        if (createHackathonDTO.getMentorEmails() != null) {
            for (String mentorEmail : createHackathonDTO.getMentorEmails()) {
                StaffProfile mentor = staffProfileRepository.findByEmail(mentorEmail);
                if (mentor == null) {
                    throw new DomainException("Nessun profilo staff trovato per l'email del mentore: " + mentorEmail);
                }
                mentorsId.add(mentor.getId());
            }
        }

        IHackathonBuilder builder = new HackathonBuilder();
        Hackathon createdHackathon = builder
                .buildOrganizer(organizer.getId())
                .buildName(createHackathonDTO.getName())
                .buildType(createHackathonDTO.getType())
                .buildRegulation(createHackathonDTO.getRegulation())
                .buildLocation(createHackathonDTO.getLocation())
                .buildOnline(createHackathonDTO.isOnline())
                .buildPrize(createHackathonDTO.getPrize())
                .buildMaxTeamSize(createHackathonDTO.getMaxTeamSize())
                .buildSubscriptionDates(createHackathonDTO.getSubscriptionDates())
                .buildDates(createHackathonDTO.getDates())
                .buildDelivery(createHackathonDTO.getDelivery())
                .buildJudge(judge.getId())
                .buildMentors(mentorsId)
                .buildRankingPolicy(createHackathonDTO.getRankingPolicy())
                .build();

        hackathonRepository.save(createdHackathon);
    }

    @Transactional
    public void closeRegistration(Long staffProfileId, Long hackathonId) {
        if (!hackathonRepository.existsOrganizer(hackathonId, staffProfileId)) {
            throw new DomainException("Solo l'organizzatore può chiudere le iscrizioni");
        }
        Hackathon hackathon = hackathonRepository.findById(hackathonId)
                .orElseThrow(() -> new DomainException("Hackathon non trovato"));
        hackathon.closeRegistration();
        hackathonRepository.save(hackathon);
    }

    @Transactional
    public void startEvent(Long staffProfileId, Long hackathonId) {
        if (!hackathonRepository.existsOrganizer(hackathonId, staffProfileId)) {
            throw new DomainException("Solo l'organizzatore può avviare l'hackathon");
        }
        Hackathon hackathon = hackathonRepository.findById(hackathonId)
                .orElseThrow(() -> new DomainException("Hackathon non trovato"));
        hackathon.start();
        hackathonRepository.save(hackathon);
    }

    @Transactional
    public void startEvaluation(Long judgeId, Long hackathonId) {
        if (!hackathonRepository.existsJudge(hackathonId, judgeId)) {
            throw new DomainException("Solo il giudice assegnato può avviare la fase di valutazione");
        }
        Hackathon hackathon = hackathonRepository.findById(hackathonId)
                .orElseThrow(() -> new DomainException("Hackathon non trovato"));
        hackathon.startEvaluation();
        hackathonRepository.save(hackathon);
    }

    @Transactional
    public PaymentResult sendPrizeToWinner(Long staffProfileId, Long hackathonId) {
        if (!hackathonRepository.existsOrganizer(hackathonId, staffProfileId)) {
            throw new DomainException("Operazione non consentita: non sei l'organizzatore di questo hackathon");
        }

        Hackathon hackathon = hackathonRepository.findByIdForUpdate(hackathonId)
                .orElseThrow(() -> new DomainException("Hackathon non trovato"));
        if (hackathon.getStatus() != HackathonStatus.CLOSED) {
            throw new DomainException("L'hackathon non è ancora chiuso");
        }
        Long participatingTeamId = hackathon.getWinnerParticipatingTeamId();
        if (participatingTeamId == null) {
            throw new DomainException("Nessun team vincitore assegnato a questo hackathon");
        }
        if (hackathon.getPrizeStatus() == PrizeStatus.PAID) {
            throw new DomainException("Il premio è già stato erogato");
        }
        if (hackathon.getPrize() <= 0) {
            hackathon.confirmPrizePaid("NO_PRIZE", LocalDateTime.now());
            hackathonRepository.save(hackathon);
            return PaymentResult.ok("NO_PRIZE");
        }

        ParticipatingTeam participatingTeam = participatingTeamRepository.getByIdAndHackathonId(participatingTeamId, hackathonId);
        PayoutAccountRef destination = participatingTeam.getPaymentAccountRef();
        PaymentResult result = paymentService.transfer(hackathon.getPrize(), destination);

        if (result.isSuccess()) {
            hackathon.confirmPrizePaid(result.getTransactionId(), LocalDateTime.now());
        } else {
            hackathon.markPrizeFailed(result.getErrorMessage(), LocalDateTime.now());
        }
        hackathonRepository.save(hackathon);
        return result;
    }

    @Transactional
    public void declareWinner(Long staffProfileId, Long hackathonId) {
        if (!hackathonRepository.existsOrganizer(hackathonId, staffProfileId)) {
            throw new DomainException("Operazione non autorizzata");
        }

        HackathonStatus hackathonStatus = hackathonRepository.findStatusByHackathonId(hackathonId);
        if (hackathonStatus != HackathonStatus.CLOSED) {
            throw new DomainException("L'hackathon deve essere chiuso per proclamare il vincitore");
        }

        List<ParticipatingTeam> eligibleParticipatingTeams = participatingTeamRepository.findEligibleForRanking(hackathonId);
        List<RankingCandidate> candidates = new ArrayList<>();

        for (ParticipatingTeam pt : eligibleParticipatingTeams) {
            Submission s = submissionRepository.findByHackathonIdAndParticipatingTeamId(hackathonId, pt.getId());
            if (s != null && s.hasEvaluation()) {
                int finalScore = s.getScore() - pt.getTotalPenaltyPoints();
                candidates.add(new RankingCandidate(
                        pt.getId(),
                        finalScore,
                        s.getUpdatedAt(),
                        pt.getRegisteredAt(),
                        pt.getTeamSize()
                ));
            }
        }

        if (candidates.isEmpty()) {
            throw new DomainException("Nessun candidato valido per la classifica");
        }

        Hackathon h = hackathonRepository.findById(hackathonId)
                .orElseThrow(() -> new DomainException("Hackathon non trovato"));

        if (h.getWinnerParticipatingTeamId() != null) {
            throw new DomainException("Il vincitore è già stato proclamato per questo hackathon");
        }
        Long winnerParticipatingTeamId = winnerService.selectWinner(h.getRankingPolicy(), candidates);
        h.declareWinner(winnerParticipatingTeamId);
        hackathonRepository.save(h);
    }

    @Transactional(readOnly = true)
    public Page<Hackathon> searchHackathon(HackathonSearchCriteria criteria, Pageable pageable) {
        hackathonValidator.validate(criteria);
        Specification<Hackathon> specification = (root, query, cb) -> {
            List<jakarta.persistence.criteria.Predicate> predicates = new ArrayList<>();
            if (criteria.nameContains() != null && !criteria.nameContains().isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + criteria.nameContains().trim().toLowerCase() + "%"));
            }
            if (criteria.status() != null) {
                predicates.add(cb.equal(root.get("status"), criteria.status()));
            }
            if (criteria.isOnline() != null) {
                predicates.add(cb.equal(root.get("online"), criteria.isOnline()));
            }
            if (criteria.startsBefore() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("dates").<LocalDateTime>get("startAt"),
                        criteria.startsBefore().atTime(23, 59, 59, 999_999_999)));
            }
            if (criteria.startsAfter() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("dates").<LocalDateTime>get("startAt"),
                        criteria.startsAfter().atStartOfDay()));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        return hackathonRepository.findAll(specification, pageable);
    }

    @Transactional(readOnly = true)
    public List<LeaderboardEntry> getLeaderboard(Long hackathonId) {
        Hackathon hackathon = hackathonRepository.findById(hackathonId)
                .orElseThrow(() -> new DomainException("Hackathon non trovato"));
        if (hackathon.getStatus() != HackathonStatus.CLOSED) {
            throw new DomainException("La classifica definitiva è disponibile dopo la chiusura delle valutazioni");
        }

        List<RankedTeam> ranked = new ArrayList<>();
        for (ParticipatingTeam participant : participatingTeamRepository.findEligibleForRanking(hackathonId)) {
            Submission submission = submissionRepository.findByHackathonIdAndParticipatingTeamId(hackathonId, participant.getId());
            if (submission == null || !submission.hasEvaluation()) continue;
            Team team = teamRepository.findById(participant.getTeam()).orElse(null);
            if (team == null) continue;
            int penaltyPoints = participant.getTotalPenaltyPoints();
            ranked.add(new RankedTeam(participant, submission, team.getName(), penaltyPoints,
                    submission.getScore() - penaltyPoints));
        }

        Comparator<RankedTeam> ordering = Comparator.comparingInt(RankedTeam::finalScore).reversed();
        switch (hackathon.getRankingPolicy()) {
            case SCORE_DESC_THEN_EARLIEST_SUBMISSION -> ordering = ordering
                    .thenComparing(item -> item.submission().getUpdatedAt());
            case SCORE_DESC_THEN_TEAM_REGISTRATION_ORDER -> ordering = ordering
                    .thenComparing(item -> item.participant().getRegisteredAt());
            case SCORE_DESC_THEN_MINOR_TEAM_SIZE_THEN_EARLIEST_SUBMISSION -> ordering = ordering
                    .thenComparingInt(item -> item.participant().getTeamSize())
                    .thenComparing(item -> item.submission().getUpdatedAt());
        }
        ranked.sort(ordering.thenComparing(item -> item.participant().getId()));

        List<LeaderboardEntry> result = new ArrayList<>();
        for (int i = 0; i < ranked.size(); i++) {
            RankedTeam item = ranked.get(i);
            result.add(new LeaderboardEntry(i + 1, item.participant().getId(), item.participant().getTeam(),
                    item.teamName(), item.submission().getScore(), item.penaltyPoints(), item.finalScore(),
                    item.submission().getUpdatedAt(), item.participant().getRegisteredAt(),
                    item.participant().getId().equals(hackathon.getWinnerParticipatingTeamId())));
        }
        return List.copyOf(result);
    }

    private record RankedTeam(ParticipatingTeam participant, Submission submission, String teamName,
                              int penaltyPoints, int finalScore) { }

    public PublicHackathonView toPublicView(Hackathon hackathon) {
        return new PublicHackathonView(
                hackathon.getId(), hackathon.getName(), hackathon.getType(), hackathon.getLocation(),
                hackathon.isOnline(), hackathon.getPrize(), hackathon.getMaxTeamSize(), hackathon.getRegulation(),
                hackathon.getOrganizer(), hackathon.getJudge(), hackathon.getSubscriptionDates(), hackathon.getDates(),
                hackathon.getDelivery(), hackathon.getRankingPolicy(), hackathon.getStatus(),
                hackathon.getWinnerParticipatingTeamId(), hackathon.getPrizeStatus());
    }

    @Transactional(readOnly = true)
    public Hackathon getHackathonDetails(Long hackathonId) {
        return hackathonRepository.findById(hackathonId)
                .orElseThrow(() -> new DomainException("Hackathon non trovato con ID: " + hackathonId));
    }
}