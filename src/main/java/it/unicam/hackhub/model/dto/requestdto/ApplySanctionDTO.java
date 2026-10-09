package it.unicam.hackhub.model.dto.requestdto;

import it.unicam.hackhub.model.enums.ReportResolution;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApplySanctionDTO {
    @NotNull private ReportResolution sanctionType;
    @NotBlank @Size(max = 500) private String reason;
    @Min(0) private int points;
}