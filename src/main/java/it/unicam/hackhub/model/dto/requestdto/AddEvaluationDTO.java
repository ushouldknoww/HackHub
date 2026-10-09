package it.unicam.hackhub.model.dto.requestdto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddEvaluationDTO {
    @Min(0) @Max(10) private int score;
    @NotBlank @Size(max = 2000) private String comment;
}