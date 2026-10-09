package it.unicam.hackhub.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterStaffRequest(
        @NotBlank @Email @Size(max = 254) String email,
        @NotBlank @Size(max = 80) String name,
        @NotBlank @Size(max = 80) String surname,
        @NotBlank @Size(min = 12, max = 128) String password
) { }
