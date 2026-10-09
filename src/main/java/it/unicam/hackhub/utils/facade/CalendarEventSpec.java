package it.unicam.hackhub.utils.facade;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Duration;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class CalendarEventSpec {
    private String title;
    private String description;
    private LocalDateTime startsAt;
    private Duration duration;
    private String inviteeEmail;
}