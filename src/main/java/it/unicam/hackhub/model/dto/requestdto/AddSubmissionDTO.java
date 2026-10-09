package it.unicam.hackhub.model.dto.requestdto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddSubmissionDTO {
    @NotBlank @Size(max = 10000) private String response;
    @NotBlank @Size(max = 500) @Pattern(regexp = "https?://.+", message = "responseURL must be an http(s) URL") private String responseURL;
}