package it.unicam.hackhub.model.dto.requestdto;

import it.unicam.hackhub.model.enums.RankingPolicy;
import it.unicam.hackhub.model.valueobjs.Period;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateHackathonDTO {
    @NotBlank @Size(max = 160) private String name;
    @NotBlank @Size(max = 100) private String type;
    @NotBlank @Size(max = 2000) private String regulation;
    private boolean isOnline;
    @Size(max = 200) private String location;
    @NotBlank @Size(max = 100) private String delivery;
    @Min(0) private double prize;
    @Positive private int maxTeamSize;
    @NotNull @Valid private Period subscriptionDates;
    @NotNull @Valid private Period dates;
    @NotBlank @Email @Size(max = 254) private String judgeEmail;
    private List<String> mentorEmails;
    @NotNull private RankingPolicy rankingPolicy;
}