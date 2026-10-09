package it.unicam.hackhub.controller;

import it.unicam.hackhub.model.Submission;
import it.unicam.hackhub.model.dto.requestdto.AddSubmissionDTO;
import it.unicam.hackhub.security.ActorPrincipal;
import it.unicam.hackhub.service.SubmissionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hackathons/{hackathonId}/submissions")
public class SubmissionController {
    private final SubmissionService submissionService;
    public SubmissionController(SubmissionService submissionService) { this.submissionService = submissionService; }

    @PostMapping
    @PreAuthorize("hasRole('PARTICIPANT')")
    public ResponseEntity<Void> create(@AuthenticationPrincipal ActorPrincipal actor,
                                       @PathVariable Long hackathonId, @Valid @RequestBody AddSubmissionDTO dto) {
        submissionService.createSubmission(actor.getSubjectId(), hackathonId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<List<Submission>> list(@AuthenticationPrincipal ActorPrincipal actor, @PathVariable Long hackathonId) {
        return ResponseEntity.ok(submissionService.getSubmissionsList(actor.getSubjectId(), hackathonId));
    }

    @GetMapping("/{subId}")
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<Submission> getDetails(@AuthenticationPrincipal ActorPrincipal actor,
                                                  @PathVariable Long hackathonId, @PathVariable Long subId) {
        return ResponseEntity.ok(submissionService.getSubmissionDetails(actor.getSubjectId(), hackathonId, subId));
    }
}
