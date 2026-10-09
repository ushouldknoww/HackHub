package it.unicam.hackhub.controller;

import it.unicam.hackhub.model.Hackathon;
import it.unicam.hackhub.model.dto.requestdto.*;
import it.unicam.hackhub.security.ActorPrincipal;
import it.unicam.hackhub.service.HackathonService;
import it.unicam.hackhub.service.EvaluationService;
import it.unicam.hackhub.model.dto.requestdto.PaymentResult;
import it.unicam.hackhub.model.dto.response.LeaderboardEntry;
import it.unicam.hackhub.model.dto.response.PublicHackathonView;
import it.unicam.hackhub.service.ParticipatingTeamService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/hackathons")
public class HackathonController {
    private final HackathonService hackathonService;
    private final ParticipatingTeamService participatingTeamService;
    private final EvaluationService evaluationService;

    public HackathonController(HackathonService hackathonService, ParticipatingTeamService participatingTeamService,
                               EvaluationService evaluationService) {
        this.hackathonService = hackathonService;
        this.participatingTeamService = participatingTeamService;
        this.evaluationService = evaluationService;
    }

    @PostMapping
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<String> create(@AuthenticationPrincipal ActorPrincipal actor,
                                         @Valid @RequestBody CreateHackathonDTO dto) {
        hackathonService.createHackathon(actor.getSubjectId(), dto);
        return ResponseEntity.status(HttpStatus.CREATED).body("Hackathon creato.");
    }

    @GetMapping("/search")
    public ResponseEntity<Page<PublicHackathonView>> search(@ModelAttribute HackathonSearchCriteria criteria,
            @PageableDefault(size = 20, sort = "id") Pageable pageable) {
        Set<String> sortableFields = Set.of("id", "name", "prize", "status", "online");
        boolean unsupportedSort = pageable.getSort().stream()
                .anyMatch(order -> !sortableFields.contains(order.getProperty()));
        Pageable safePageable = unsupportedSort
                ? PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("id").ascending())
                : pageable;
        return ResponseEntity.ok(hackathonService.searchHackathon(criteria, safePageable)
                .map(hackathonService::toPublicView));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PublicHackathonView> getDetails(@PathVariable Long id) {
        return ResponseEntity.ok(hackathonService.toPublicView(hackathonService.getHackathonDetails(id)));
    }

    @GetMapping("/{id}/leaderboard")
    public ResponseEntity<List<LeaderboardEntry>> leaderboard(@PathVariable Long id) {
        return ResponseEntity.ok(hackathonService.getLeaderboard(id));
    }

    @PostMapping("/{id}/register")
    @PreAuthorize("hasRole('PARTICIPANT')")
    public ResponseEntity<String> register(@AuthenticationPrincipal ActorPrincipal actor,
                                           @PathVariable Long id, @Valid @RequestBody RegisterTeamDTO dto) {
        participatingTeamService.registerTeamToHackathon(actor.getSubjectId(), id, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body("Iscritto.");
    }


    @PostMapping("/{id}/close-registration")
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<Void> closeRegistration(@AuthenticationPrincipal ActorPrincipal actor, @PathVariable Long id) {
        hackathonService.closeRegistration(actor.getSubjectId(), id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/start")
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<Void> startEvent(@AuthenticationPrincipal ActorPrincipal actor, @PathVariable Long id) {
        hackathonService.startEvent(actor.getSubjectId(), id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/start-evaluation")
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<Void> startEvaluation(@AuthenticationPrincipal ActorPrincipal actor, @PathVariable Long id) {
        hackathonService.startEvaluation(actor.getSubjectId(), id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/evaluations/confirm")
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<Void> confirmEvaluations(@AuthenticationPrincipal ActorPrincipal actor, @PathVariable Long id) {
        evaluationService.confirmEvaluations(actor.getSubjectId(), id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/prize-payout")
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<PaymentResult> payWinner(@AuthenticationPrincipal ActorPrincipal actor, @PathVariable Long id) {
        PaymentResult result = hackathonService.sendPrizeToWinner(actor.getSubjectId(), id);
        return result.isSuccess() ? ResponseEntity.ok(result) : ResponseEntity.status(502).body(result);
    }

    @PostMapping("/{id}/declare-winner")
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<String> declareWinner(@AuthenticationPrincipal ActorPrincipal actor, @PathVariable Long id) {
        hackathonService.declareWinner(actor.getSubjectId(), id);
        return ResponseEntity.ok("Vincitore proclamato.");
    }
}
