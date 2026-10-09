package it.unicam.hackhub.service;

import it.unicam.hackhub.model.ParticipatingTeam;
import it.unicam.hackhub.model.SupportRequest;
import it.unicam.hackhub.model.dto.CallBookingRequest;
import it.unicam.hackhub.model.dto.requestdto.BookSupportCallDTO;
import it.unicam.hackhub.model.dto.requestdto.CallBookingResult;
import it.unicam.hackhub.model.dto.requestdto.CreateSupportRequestDTO;
import it.unicam.hackhub.model.dto.requestdto.ReplySupportRequestDTO;
import it.unicam.hackhub.model.enums.HackathonStatus;
import it.unicam.hackhub.repository.HackathonRepository;
import it.unicam.hackhub.repository.ParticipatingTeamRepository;
import it.unicam.hackhub.repository.SupportRequestRepository;
import it.unicam.hackhub.utils.DomainException;
import it.unicam.hackhub.utils.facade.ICalendarService;
import it.unicam.hackhub.validators.SupportRequestValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SupportRequestService {

    private final SupportRequestValidator supportRequestValidator;
    private final ParticipatingTeamRepository participatingTeamRepository;
    private final SupportRequestRepository supportRequestRepository;
    private final HackathonRepository hackathonRepository;
    private final ICalendarService calendarService;

    public SupportRequestService(SupportRequestValidator supportRequestValidator, 
                                 ParticipatingTeamRepository participatingTeamRepository, 
                                 SupportRequestRepository supportRequestRepository, 
                                 HackathonRepository hackathonRepository, 
                                 ICalendarService calendarService) {
        this.supportRequestValidator = supportRequestValidator;
        this.participatingTeamRepository = participatingTeamRepository;
        this.supportRequestRepository = supportRequestRepository;
        this.hackathonRepository = hackathonRepository;
        this.calendarService = calendarService;
    }

    @Transactional
    public void createSupportRequest(Long userId, Long hackathonId, CreateSupportRequestDTO dto) {
        supportRequestValidator.validate(dto);
        HackathonStatus hackathonStatus = hackathonRepository.findStatusByHackathonId(hackathonId);
        if (hackathonStatus != HackathonStatus.RUNNING) {
            throw new DomainException("Impossibile aprire un ticket: l'hackathon non è attualmente in corso");
        }
        
        ParticipatingTeam pt = participatingTeamRepository.findByHackathonIdAndActiveMemberId(hackathonId, userId);
        if (pt == null) {
            throw new DomainException("Non sei un membro attivo di un team iscritto a questo hackathon");
        }
        
        SupportRequest request = new SupportRequest(
                hackathonId,
                pt.getId(),
                dto.getTitle(),
                dto.getDescription(),
                dto.getUrgency(),
                LocalDateTime.now()
        );
        supportRequestRepository.save(request);
    }

    @Transactional(readOnly = true)
    public List<SupportRequest> getSupportRequests(Long staffProfileId, Long hackathonId) {
        if (!hackathonRepository.existsMentor(hackathonId, staffProfileId)) {
            throw new DomainException("Operazione non autorizzata: non sei un mentore dell'hackathon");
        }
        return supportRequestRepository.getByHackathonId(hackathonId);
    }

    @Transactional(readOnly = true)
    public SupportRequest getSupportRequestDetails(Long staffProfileId, Long hackathonId, Long supportRequestId) {
        if (!hackathonRepository.existsMentor(hackathonId, staffProfileId)) {
            throw new DomainException("Operazione non autorizzata: non sei un mentore dell'hackathon");
        }

        SupportRequest supportRequest = supportRequestRepository.getByIdAndHackathonId(supportRequestId, hackathonId);
        if (supportRequest == null) {
            throw new DomainException("La richiesta di supporto non appartiene all'hackathon selezionato");
        }
        return supportRequest;
    }

    @Transactional
    public void replyToSupportRequest(Long staffProfileId, Long hackathonId, Long supportRequestId, ReplySupportRequestDTO dto) {
        supportRequestValidator.validate(dto);

        if (!hackathonRepository.existsMentor(hackathonId, staffProfileId)) {
            throw new DomainException("Operazione non autorizzata: non sei un mentore per questo hackathon");
        }

        HackathonStatus hackathonStatus = hackathonRepository.findStatusByHackathonId(hackathonId);
        if (hackathonStatus != HackathonStatus.RUNNING) {
            throw new DomainException("L'hackathon non è attualmente in corso");
        }

        SupportRequest supportRequest = supportRequestRepository.getByIdAndHackathonId(supportRequestId, hackathonId);
        if (supportRequest == null) {
            throw new DomainException("Richiesta di supporto non trovata");
        }

        if (!supportRequest.isOpen()) {
            throw new DomainException("La richiesta di supporto è già stata risolta");
        }

        supportRequest.addReply(staffProfileId, dto.getMessage(), LocalDateTime.now());
        supportRequestRepository.save(supportRequest);
    }

    @Transactional
    public void bookSupportCall(Long staffProfileId, Long hackathonId, Long supportRequestId, BookSupportCallDTO bookSupportCallDTO) {
        supportRequestValidator.validate(bookSupportCallDTO);
        if (!hackathonRepository.existsMentor(hackathonId, staffProfileId)) {
            throw new DomainException("L'utente non è un mentore per questo hackathon");
        }
        HackathonStatus hackathonStatus = hackathonRepository.findStatusByHackathonId(hackathonId);
        if (hackathonStatus != HackathonStatus.RUNNING) {
            throw new DomainException("L'hackathon non è in corso");
        }
        SupportRequest supportRequest = supportRequestRepository.getByIdAndHackathonId(supportRequestId, hackathonId);
        if (supportRequest == null) {
            throw new DomainException("Richiesta di supporto non trovata");
        }
        if (!supportRequest.isOpen()) {
            throw new DomainException("La richiesta di supporto è già stata risolta");
        }
        
        Long participatingTeamId = supportRequest.getParticipatingTeam();
        ParticipatingTeam participatingTeam = participatingTeamRepository.getByIdAndHackathonId(participatingTeamId, hackathonId);
        if (participatingTeam == null) {
            throw new DomainException("Team partecipante non trovato");
        }
        
        String attendeeEmail = participatingTeam.getContactEmail();
        CallBookingRequest callBookingRequest = new CallBookingRequest(
                staffProfileId,
                bookSupportCallDTO.getTitle(),
                bookSupportCallDTO.getDescription(),
                bookSupportCallDTO.getStartsAt(),
                bookSupportCallDTO.getDuration(),
                attendeeEmail
        );
        
        CallBookingResult callBookingResult = calendarService.scheduleCall(callBookingRequest);
        if (!callBookingResult.isSuccess()) {
            throw new DomainException("Errore nella pianificazione della call: " + callBookingResult.getFailureReason());
        }
        
        supportRequest.scheduleCall(
                staffProfileId,
                bookSupportCallDTO.getStartsAt(),
                bookSupportCallDTO.getDuration(),
                callBookingResult.getEventId(),
                callBookingResult.getMeetingURL()
        );
        supportRequestRepository.save(supportRequest);
    }
}