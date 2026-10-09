package it.unicam.hackhub.validators;

import it.unicam.hackhub.model.dto.requestdto.ApplySanctionDTO;
import it.unicam.hackhub.model.dto.requestdto.CreateReportDTO;
import it.unicam.hackhub.model.enums.ReportResolution;
import org.springframework.stereotype.Component;

@Component
public class ReportValidator {

    public void validate(CreateReportDTO dto) {
        if (dto == null) throw new IllegalArgumentException("Il DTO non può essere nullo");

        if (dto.getReason() == null || dto.getReason().trim().isBlank()) {
            throw new IllegalArgumentException("Il motivo della segnalazione è obbligatorio");
        }
        if (dto.getUrgency() == null) {
            throw new IllegalArgumentException("Il livello di urgenza è obbligatorio");
        }
    }

    public void validate(ApplySanctionDTO dto) {
        if (dto == null) throw new IllegalArgumentException("Il DTO non può essere nullo");

        if (dto.getSanctionType() == null) {
            throw new IllegalArgumentException("Il tipo di sanzione è obbligatorio");
        }
        if (dto.getReason() == null || dto.getReason().trim().isBlank()) {
            throw new IllegalArgumentException("Il motivo della sanzione è obbligatorio");
        }

        if (dto.getSanctionType() == ReportResolution.POINT_DEDUCTION && dto.getPoints() <= 0) {
            throw new IllegalArgumentException("I punti di penalità devono essere maggiori di zero per una decurtazione");
        }
    }
}