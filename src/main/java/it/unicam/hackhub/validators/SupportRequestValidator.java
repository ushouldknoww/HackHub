package it.unicam.hackhub.validators;

import it.unicam.hackhub.model.dto.requestdto.CreateSupportRequestDTO;
import it.unicam.hackhub.model.dto.requestdto.ReplySupportRequestDTO;
import it.unicam.hackhub.model.dto.requestdto.BookSupportCallDTO;
import org.springframework.stereotype.Component;

@Component
public class SupportRequestValidator {

    public void validate(CreateSupportRequestDTO dto) {
        if (dto == null) throw new IllegalArgumentException("Il DTO non può essere nullo");

        if (dto.getTitle() == null || dto.getTitle().trim().isBlank()) {
            throw new IllegalArgumentException("Il titolo della richiesta è obbligatorio");
        }
        if (dto.getDescription() == null || dto.getDescription().trim().isBlank()) {
            throw new IllegalArgumentException("La descrizione è obbligatoria");
        }
        if (dto.getUrgency() == null) {
            throw new IllegalArgumentException("L'urgenza è obbligatoria");
        }
    }

    public void validate(ReplySupportRequestDTO dto) {
        if (dto == null) throw new IllegalArgumentException("Il DTO non può essere nullo");

        if (dto.getMessage() == null || dto.getMessage().trim().isBlank()) {
            throw new IllegalArgumentException("Il messaggio di risposta è obbligatorio");
        }
    }

    public void validate(BookSupportCallDTO dto) {
        if (dto == null) throw new IllegalArgumentException("Il DTO non può essere nullo");

        if (dto.getStartsAt() == null) {
            throw new IllegalArgumentException("La data e l'ora di inizio della call sono obbligatorie");
        }
        if (dto.getDuration() == null || dto.getDuration().isNegative() || dto.getDuration().isZero()) {
            throw new IllegalArgumentException("La durata della call è obbligatoria e deve essere maggiore di zero");
        }
        if (dto.getTitle() == null || dto.getTitle().trim().isBlank()) {
            throw new IllegalArgumentException("Il titolo della call è obbligatorio");
        }
        if (dto.getDescription() == null || dto.getDescription().trim().isBlank()) {
            throw new IllegalArgumentException("La descrizione della call è obbligatoria");
        }
    }
}