package it.unicam.hackhub.model.dto.requestdto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Duration;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookSupportCallDTO {
    @NotNull private LocalDateTime startsAt;
    @NotNull private Duration duration;
    @NotBlank @Size(max = 150) private String title;
    @NotBlank @Size(max = 2000) private String description;
}