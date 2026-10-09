package it.unicam.hackhub.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class CallBookingRequest {

    private Long mentor;
    private String title;
    private String description;
    private LocalDateTime startsAt;
    private Duration duration;
    private String attendeeEmail;
    
}