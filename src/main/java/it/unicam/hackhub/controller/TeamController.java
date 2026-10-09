package it.unicam.hackhub.controller;

import it.unicam.hackhub.model.dto.requestdto.CreateTeamDTO;
import it.unicam.hackhub.security.ActorPrincipal;
import it.unicam.hackhub.service.TeamService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/teams")
@PreAuthorize("hasRole('PARTICIPANT')")
public class TeamController {
    private final TeamService teamService;
    public TeamController(TeamService teamService) { this.teamService = teamService; }

    @PostMapping
    public ResponseEntity<String> createTeam(@AuthenticationPrincipal ActorPrincipal actor,
                                             @Valid @RequestBody CreateTeamDTO dto) {
        teamService.createTeam(actor.getSubjectId(), dto);
        return ResponseEntity.status(HttpStatus.CREATED).body("Team creato.");
    }
}
