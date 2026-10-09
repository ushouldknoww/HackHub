package it.unicam.hackhub.validators;

import it.unicam.hackhub.model.dto.requestdto.AddSubmissionDTO;
import org.springframework.stereotype.Component;

@Component
public class SubmissionValidator {

    public void validate(AddSubmissionDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("DTO nullo");
        }

        if (dto.getResponse() == null || dto.getResponse().isBlank()) {
            throw new IllegalArgumentException("Testo risposta mancante");
        }
    }
}