package it.unicam.hackhub.controller;

import it.unicam.hackhub.model.SupportRequest;
import it.unicam.hackhub.model.dto.requestdto.BookSupportCallDTO;
import it.unicam.hackhub.model.dto.requestdto.CreateSupportRequestDTO;
import it.unicam.hackhub.model.dto.requestdto.ReplySupportRequestDTO;
import it.unicam.hackhub.security.ActorPrincipal;
import it.unicam.hackhub.service.SupportRequestService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hackathons/{hackathonId}/support-requests")
public class SupportRequestController {
    private final SupportRequestService supportRequestService;
    public SupportRequestController(SupportRequestService supportRequestService) { this.supportRequestService = supportRequestService; }

    @PostMapping
    @PreAuthorize("hasRole('PARTICIPANT')")
    public ResponseEntity<Void> create(@AuthenticationPrincipal ActorPrincipal actor,
                                       @PathVariable Long hackathonId, @Valid @RequestBody CreateSupportRequestDTO dto) {
        supportRequestService.createSupportRequest(actor.getSubjectId(), hackathonId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<List<SupportRequest>> list(@AuthenticationPrincipal ActorPrincipal actor, @PathVariable Long hackathonId) {
        return ResponseEntity.ok(supportRequestService.getSupportRequests(actor.getSubjectId(), hackathonId));
    }

    @GetMapping("/{reqId}")
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<SupportRequest> getDetails(@AuthenticationPrincipal ActorPrincipal actor,
                                                      @PathVariable Long hackathonId, @PathVariable Long reqId) {
        return ResponseEntity.ok(supportRequestService.getSupportRequestDetails(actor.getSubjectId(), hackathonId, reqId));
    }

    @PostMapping("/{reqId}/reply")
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<Void> reply(@AuthenticationPrincipal ActorPrincipal actor,
                                      @PathVariable Long hackathonId, @PathVariable Long reqId,
                                      @Valid @RequestBody ReplySupportRequestDTO dto) {
        supportRequestService.replyToSupportRequest(actor.getSubjectId(), hackathonId, reqId, dto);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{reqId}/calls")
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<Void> bookCall(@AuthenticationPrincipal ActorPrincipal actor,
                                                       @PathVariable Long hackathonId, @PathVariable Long reqId,
                                                       @Valid @RequestBody BookSupportCallDTO dto) {
        supportRequestService.bookSupportCall(actor.getSubjectId(), hackathonId, reqId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
