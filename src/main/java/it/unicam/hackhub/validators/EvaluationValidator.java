package it.unicam.hackhub.validators;

import it.unicam.hackhub.model.dto.requestdto.AddEvaluationDTO;
import org.springframework.stereotype.Component;

@Component
public class EvaluationValidator {

    public void validate(AddEvaluationDTO dto, Long staffProfileId, Long submissionId) {
        if (dto == null) {
            throw new IllegalArgumentException("Il DTO non può essere nullo");
        }
        if (staffProfileId == null || submissionId == null) {
            throw new IllegalArgumentException("Gli ID dello staff e della sottomissione sono obbligatori");
        }
        if (dto.getScore() < 0 || dto.getScore() > 10) {
            throw new IllegalArgumentException("Il punteggio deve essere compreso tra 0 e 10");
        }
        if (dto.getComment() == null || dto.getComment().trim().isBlank()) {
            throw new IllegalArgumentException("Il commento della valutazione è obbligatorio");
        }
    }
}