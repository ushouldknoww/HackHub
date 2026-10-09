package it.unicam.hackhub;

import it.unicam.hackhub.model.Hackathon;
import it.unicam.hackhub.model.enums.HackathonStatus;
import it.unicam.hackhub.model.enums.RankingPolicy;
import it.unicam.hackhub.model.valueobjs.Period;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class HackathonLifecycleTest {
    @Test
    void advancesOnlyThroughValidLifecycleStates() {
        LocalDateTime start = LocalDateTime.now().minusDays(3);
        Hackathon hackathon = new Hackathon(
                "Lifecycle test", "Software", "Online", true, 0, 4, "Rules",
                1L, 2L, List.of(), "URL", RankingPolicy.SCORE_DESC_THEN_EARLIEST_SUBMISSION,
                new Period(start.minusDays(5), start.minusDays(4)),
                new Period(start, start.plusDays(1)));

        assertEquals(HackathonStatus.IN_REGISTRATION, hackathon.getStatus());
        hackathon.closeRegistration();
        assertEquals(HackathonStatus.READY_TO_START, hackathon.getStatus());
        hackathon.start();
        assertEquals(HackathonStatus.RUNNING, hackathon.getStatus());
        hackathon.startEvaluation();
        assertEquals(HackathonStatus.IN_EVALUATION, hackathon.getStatus());
        hackathon.close();
        assertEquals(HackathonStatus.CLOSED, hackathon.getStatus());
    }

    @Test
    void rejectsSkippingLifecycleStates() {
        LocalDateTime future = LocalDateTime.now().plusDays(3);
        Hackathon hackathon = new Hackathon(
                "Lifecycle test", "Software", "Online", true, 0, 4, "Rules",
                1L, 2L, List.of(), "URL", RankingPolicy.SCORE_DESC_THEN_EARLIEST_SUBMISSION,
                new Period(future.minusDays(5), future.minusDays(4)),
                new Period(future, future.plusDays(1)));

        assertThrows(IllegalStateException.class, hackathon::start);
        assertThrows(IllegalStateException.class, hackathon::startEvaluation);
        assertThrows(IllegalStateException.class, hackathon::close);
    }
}
