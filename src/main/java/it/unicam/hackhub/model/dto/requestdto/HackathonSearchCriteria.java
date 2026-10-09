package it.unicam.hackhub.model.dto.requestdto;

import it.unicam.hackhub.model.enums.HackathonStatus;
import java.time.LocalDate;

public record HackathonSearchCriteria(
        String nameContains,
        HackathonStatus status,
        Boolean isOnline,
        LocalDate startsBefore,
        LocalDate startsAfter
) {
}