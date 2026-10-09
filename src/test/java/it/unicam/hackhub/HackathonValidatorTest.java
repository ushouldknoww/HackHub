package it.unicam.hackhub;

import it.unicam.hackhub.model.dto.requestdto.CreateHackathonDTO;
import it.unicam.hackhub.model.enums.RankingPolicy;
import it.unicam.hackhub.model.valueobjs.Period;
import it.unicam.hackhub.validators.HackathonValidator;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HackathonValidatorTest {
    private final HackathonValidator validator = new HackathonValidator();

    @Test
    void rejectsNegativePrize() {
        LocalDateTime now = LocalDateTime.now().plusDays(2);
        CreateHackathonDTO dto = new CreateHackathonDTO(
                "Campus Hack", "Software", "Rules", true, null, "Online", -1.0, 4,
                new Period(now, now.plusDays(1)),
                new Period(now.plusDays(1), now.plusDays(2)),
                "judge@example.com", List.of(), RankingPolicy.SCORE_DESC_THEN_EARLIEST_SUBMISSION);
        assertThrows(IllegalArgumentException.class, () -> validator.validate(dto));
    }

    @Test
    void rejectsInvertedRegistrationWindow() {
        LocalDateTime now = LocalDateTime.now().plusDays(10);
        CreateHackathonDTO dto = new CreateHackathonDTO(
                "Campus Hack", "Software", "Rules", true, null, "Online", 100.0, 4,
                new Period(now.plusDays(2), now),
                new Period(now.plusDays(3), now.plusDays(4)),
                "judge@example.com", List.of(), RankingPolicy.SCORE_DESC_THEN_EARLIEST_SUBMISSION);
        assertThrows(RuntimeException.class, () -> validator.validate(dto));
    }
}
