package it.unicam.hackhub.model.dto.requestdto;

import it.unicam.hackhub.model.enums.Urgency;
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
public class CreateReportDTO {
    @NotBlank @Size(max = 500) private String reason;
    @NotNull private Urgency urgency;
}