package it.unicam.hackhub.controller;

import it.unicam.hackhub.model.Report;
import it.unicam.hackhub.model.dto.requestdto.ApplySanctionDTO;
import it.unicam.hackhub.model.dto.requestdto.CreateReportDTO;
import it.unicam.hackhub.security.ActorPrincipal;
import it.unicam.hackhub.service.ReportService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hackathons/{hackathonId}/reports")
@PreAuthorize("hasRole('STAFF')")
public class ReportController {
    private final ReportService reportService;
    public ReportController(ReportService reportService) { this.reportService = reportService; }

    @PostMapping("/participating-teams/{teamId}")
    public ResponseEntity<Void> create(@AuthenticationPrincipal ActorPrincipal actor,
                                       @PathVariable Long hackathonId, @PathVariable Long teamId,
                                       @Valid @RequestBody CreateReportDTO dto) {
        reportService.createReport(actor.getSubjectId(), hackathonId, teamId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public ResponseEntity<List<Report>> list(@AuthenticationPrincipal ActorPrincipal actor, @PathVariable Long hackathonId) {
        return ResponseEntity.ok(reportService.getReports(actor.getSubjectId(), hackathonId));
    }

    @GetMapping("/{reportId}")
    public ResponseEntity<Report> getDetails(@AuthenticationPrincipal ActorPrincipal actor,
                                              @PathVariable Long hackathonId, @PathVariable Long reportId) {
        return ResponseEntity.ok(reportService.getReportDetails(actor.getSubjectId(), hackathonId, reportId));
    }

    @PostMapping("/{reportId}/sanction")
    public ResponseEntity<Void> applySanction(@AuthenticationPrincipal ActorPrincipal actor,
                                              @PathVariable Long hackathonId, @PathVariable Long reportId,
                                              @Valid @RequestBody ApplySanctionDTO dto) {
        reportService.applySanction(actor.getSubjectId(), hackathonId, reportId, dto);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{reportId}/archive")
    public ResponseEntity<Void> archive(@AuthenticationPrincipal ActorPrincipal actor,
                                        @PathVariable Long hackathonId, @PathVariable Long reportId) {
        reportService.archiveReport(actor.getSubjectId(), hackathonId, reportId);
        return ResponseEntity.noContent().build();
    }
}
