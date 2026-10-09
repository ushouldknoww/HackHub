package it.unicam.hackhub.validators;

import it.unicam.hackhub.model.dto.requestdto.CreateTeamDTO;
import org.springframework.stereotype.Component;

@Component
public class TeamValidator {

    public void validate(CreateTeamDTO dto) {
        if (dto == null) throw new IllegalArgumentException("Il DTO non può essere nullo");

        if (dto.getName() == null || dto.getName().trim().isBlank()) {
            throw new IllegalArgumentException("Il nome del team è obbligatorio");
        }
    }
}