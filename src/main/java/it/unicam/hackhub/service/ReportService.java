package it.unicam.hackhub.service;

import it.unicam.hackhub.model.ParticipatingTeam;
import it.unicam.hackhub.model.Report;
import it.unicam.hackhub.model.dto.requestdto.ApplySanctionDTO;
import it.unicam.hackhub.model.dto.requestdto.CreateReportDTO;
import it.unicam.hackhub.model.enums.HackathonStatus;
import it.unicam.hackhub.model.enums.ReportResolution;
import it.unicam.hackhub.repository.HackathonRepository;
import it.unicam.hackhub.repository.ParticipatingTeamRepository;
import it.unicam.hackhub.repository.ReportRepository;
import it.unicam.hackhub.utils.DomainException;
import it.unicam.hackhub.validators.ReportValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReportService {

    private final HackathonRepository hackathonRepository;
    private final ParticipatingTeamRepository participatingTeamRepository;
    private final ReportRepository reportRepository;
    private final ReportValidator reportValidator;

    public ReportService(HackathonRepository hackathonRepository, 
                         ParticipatingTeamRepository participatingTeamRepository, 
                         ReportRepository reportRepository, 
                         ReportValidator reportValidator) {
        this.hackathonRepository = hackathonRepository;
        this.participatingTeamRepository = participatingTeamRepository;
        this.reportRepository = reportRepository;
        this.reportValidator = reportValidator;
    }

    @Transactional
    public void createReport(Long staffProfileId, Long hackathonId, Long participatingTeamId, CreateReportDTO createReportDTO) {
        reportValidator.validate(createReportDTO);

        if (!hackathonRepository.existsMentor(hackathonId, staffProfileId)) {
            throw new DomainException("L'utente non è un mentore per questo hackathon");
        }

        HackathonStatus hackathonStatus = hackathonRepository.findStatusByHackathonId(hackathonId);
        if (hackathonStatus != HackathonStatus.RUNNING) {
            throw new DomainException("L'hackathon non è attualmente in corso");
        }

        ParticipatingTeam participatingTeam = participatingTeamRepository.getByIdAndHackathonId(participatingTeamId, hackathonId);
        if (participatingTeam == null) {
            throw new DomainException("Team partecipante non trovato");
        }

        Report createdReport = new Report(
                hackathonId,
                staffProfileId,
                participatingTeamId,
                createReportDTO.getReason(),
                createReportDTO.getUrgency(),
                LocalDateTime.now()
        );

        reportRepository.save(createdReport);
    }

    @Transactional(readOnly = true)
    public List<Report> getReports(Long staffProfileId, Long hackathonId) {
        if (!hackathonRepository.existsOrganizer(hackathonId, staffProfileId)) { 
            throw new DomainException("Operazione non autorizzata: non sei l'organizzatore dell'hackathon");
        }
        return reportRepository.getByHackathonId(hackathonId);
    }

    @Transactional(readOnly = true)
    public Report getReportDetails(Long staffProfileId, Long hackathonId, Long reportId) {
        if (!hackathonRepository.existsOrganizer(hackathonId, staffProfileId)) {
            throw new DomainException("Operazione non autorizzata: non sei l'organizzatore dell'hackathon");
        }

        Report report = reportRepository.getByIdAndHackathonId(reportId, hackathonId);
        if (report == null) {
            throw new DomainException("La segnalazione non appartiene all'hackathon selezionato");
        }
        return report;
    }

    @Transactional
    public void applySanction(Long staffProfileId, Long hackathonId, Long reportId, ApplySanctionDTO applySanctionDTO) {
        reportValidator.validate(applySanctionDTO);
        
        if (!hackathonRepository.existsOrganizer(hackathonId, staffProfileId)) {
            throw new DomainException("Operazione non autorizzata");
        }
        
        HackathonStatus hackathonStatus = hackathonRepository.findStatusByHackathonId(hackathonId);
        if (hackathonStatus != HackathonStatus.RUNNING && hackathonStatus != HackathonStatus.IN_EVALUATION) {
            throw new DomainException("Stato hackathon non valido per sanzioni");
        }
        
        Report report = reportRepository.getByIdAndHackathonId(reportId, hackathonId);
        if (report == null) {
            throw new DomainException("Segnalazione non trovata");
        }
        
        ParticipatingTeam pt = participatingTeamRepository.getByIdAndHackathonId(report.getParticipatingTeam(), hackathonId);
        if (report.isResolved()) {
            throw new DomainException("Segnalazione già risolta");
        }
        
        if (applySanctionDTO.getSanctionType() == ReportResolution.POINT_DEDUCTION) {
            pt.applyPenalty(applySanctionDTO.getPoints(), applySanctionDTO.getReason(), report.getId());
        } else if (applySanctionDTO.getSanctionType() == ReportResolution.TEAM_DISQUALIFICATION) {
            pt.disqualify();
        }
        
        report.resolve(applySanctionDTO.getSanctionType());
        participatingTeamRepository.save(pt);
        reportRepository.save(report);
    }

    @Transactional
    public void archiveReport(Long staffProfileId, Long hackathonId, Long reportId) {
        if (!hackathonRepository.existsOrganizer(hackathonId, staffProfileId)) {
            throw new DomainException("Operazione non autorizzata");
        }
        
        HackathonStatus hackathonStatus = hackathonRepository.findStatusByHackathonId(hackathonId);
        if (hackathonStatus != HackathonStatus.RUNNING && hackathonStatus != HackathonStatus.IN_EVALUATION) {
            throw new DomainException("Stato hackathon non valido");
        }
        
        Report report = reportRepository.getByIdAndHackathonId(reportId, hackathonId);
        if (report == null) {
            throw new DomainException("Segnalazione non trovata");
        }
        
        if (report.isResolved()) {
            throw new DomainException("Segnalazione già archiviata");
        }
        
        report.archive();
        reportRepository.save(report);
    }
}