package it.unicam.hackhub.controller;

import it.unicam.hackhub.model.dto.requestdto.AddEvaluationDTO;
import it.unicam.hackhub.security.ActorPrincipal;
import it.unicam.hackhub.service.EvaluationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/hackathons/{hackathonId}/submissions/{subId}/evaluations")
public class EvaluationController {
    private final EvaluationService evaluationService;
    public EvaluationController(EvaluationService evaluationService) { this.evaluationService = evaluationService; }

    @PostMapping
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<String> add(@AuthenticationPrincipal ActorPrincipal actor,
                                      @PathVariable Long hackathonId, @PathVariable Long subId,
                                      @Valid @RequestBody AddEvaluationDTO dto) {
        evaluationService.addEvaluation(actor.getSubjectId(), hackathonId, subId, dto);
        return ResponseEntity.ok("Valutazione salvata.");
    }
}
