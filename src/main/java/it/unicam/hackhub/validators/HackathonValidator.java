package it.unicam.hackhub.validators;

import it.unicam.hackhub.model.dto.requestdto.CreateHackathonDTO;
import it.unicam.hackhub.model.dto.requestdto.HackathonSearchCriteria;
import it.unicam.hackhub.model.valueobjs.Period;
import it.unicam.hackhub.utils.DomainException;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class HackathonValidator {

    public void validate(CreateHackathonDTO dto) {
        if (dto == null) throw new IllegalArgumentException("Il DTO non può essere nullo");

        if (dto.getName() == null || dto.getName().trim().isBlank())
            throw new IllegalArgumentException("Il nome dell'hackathon è obbligatorio");

        if (dto.getType() == null || dto.getType().trim().isBlank())
            throw new IllegalArgumentException("Il tipo di hackathon è obbligatorio");

        if (dto.getJudgeEmail() == null || dto.getJudgeEmail().trim().isBlank())
            throw new IllegalArgumentException("L'email del giudice è obbligatoria");

        if (dto.getPrize() < 0)
            throw new IllegalArgumentException("Il premio non può essere negativo");

        if (dto.getMaxTeamSize() <= 0)
            throw new IllegalArgumentException("La dimensione massima del team deve essere maggiore di zero");

        validateDates(dto.getSubscriptionDates(), dto.getDates());
    }

    public void validate(HackathonSearchCriteria s) {
        if (s == null) {
            throw new IllegalArgumentException("I criteri di ricerca non possono essere nulli");
        }

        if (s.startsAfter() != null && s.startsBefore() != null) {
            if (s.startsAfter().isAfter(s.startsBefore())) {
                throw new IllegalArgumentException("La data 'inizia dopo' non può essere successiva alla data 'inizia prima'");
            }
        }
    }

    private void validateDates(Period sub, Period hack) {
        if (sub == null || hack == null) {
            throw new IllegalArgumentException("Le date di iscrizione e svolgimento sono obbligatorie");
        }

        LocalDateTime now = LocalDateTime.now();

        if (sub.getStartAt() == null || sub.getEndAt() == null)
            throw new IllegalArgumentException("Date iscrizione incomplete");

        if (sub.getStartAt().isAfter(sub.getEndAt())) {
            throw new DomainException("La data inizio iscrizioni deve precedere la fine");
        }

        if (hack.getStartAt() == null || hack.getEndAt() == null)
            throw new IllegalArgumentException("Date hackathon incomplete");

        if (hack.getStartAt().isAfter(hack.getEndAt())) {
            throw new DomainException("La data inizio hackathon deve precedere la fine");
        }

        if (sub.getEndAt().isBefore(now)) {
            throw new DomainException("La data di fine iscrizione non può essere nel passato");
        }

        if (hack.getStartAt().isBefore(sub.getStartAt())) {
            throw new DomainException("L'hackathon non può iniziare prima dell'apertura delle iscrizioni");
        }

        if (sub.getEndAt().isAfter(hack.getEndAt())) {
            throw new DomainException("Le iscrizioni non possono terminare dopo la fine dell'hackathon");
        }
    }
}