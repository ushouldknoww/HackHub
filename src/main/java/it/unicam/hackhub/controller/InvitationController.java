package it.unicam.hackhub.controller;

import it.unicam.hackhub.model.Invitation;
import it.unicam.hackhub.security.ActorPrincipal;
import it.unicam.hackhub.service.InvitationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invitations")
@PreAuthorize("hasRole('PARTICIPANT')")
public class InvitationController {
    private final InvitationService invitationService;
    public InvitationController(InvitationService invitationService) { this.invitationService = invitationService; }

    @GetMapping
    public ResponseEntity<List<Invitation>> list(@AuthenticationPrincipal ActorPrincipal actor) {
        return ResponseEntity.ok(invitationService.getInvitationsList(actor.getSubjectId()));
    }

    @GetMapping("/{invId}")
    public ResponseEntity<Invitation> details(@AuthenticationPrincipal ActorPrincipal actor, @PathVariable Long invId) {
        Invitation invitation = invitationService.getInvitationDetails(actor.getSubjectId(), invId);
        if (invitation == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(invitation);
    }

    @PostMapping("/invite/{inviteeId}")
    public ResponseEntity<Invitation> invite(@AuthenticationPrincipal ActorPrincipal actor, @PathVariable Long inviteeId) {
        return ResponseEntity.status(201).body(invitationService.inviteUser(actor.getSubjectId(), inviteeId));
    }

    @PostMapping("/{invId}/accept")
    public ResponseEntity<Void> accept(@AuthenticationPrincipal ActorPrincipal actor, @PathVariable Long invId) {
        invitationService.acceptInvitation(actor.getSubjectId(), invId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{invId}/reject")
    public ResponseEntity<Void> reject(@AuthenticationPrincipal ActorPrincipal actor, @PathVariable Long invId) {
        invitationService.rejectInvitation(actor.getSubjectId(), invId);
        return ResponseEntity.noContent().build();
    }
}
