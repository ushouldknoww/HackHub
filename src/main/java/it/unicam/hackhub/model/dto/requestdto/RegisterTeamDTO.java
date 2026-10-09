package it.unicam.hackhub.model.dto.requestdto;

import it.unicam.hackhub.model.enums.PayoutMethod;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterTeamDTO {

    @NotBlank @Email @Size(max = 254) private String contactEmail;
    @NotNull private PayoutMethod payoutMethod;
    @NotBlank @Size(max = 300) private String payoutRef;

}