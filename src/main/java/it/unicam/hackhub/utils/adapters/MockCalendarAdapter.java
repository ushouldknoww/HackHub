package it.unicam.hackhub.utils.adapters;

import it.unicam.hackhub.model.dto.requestdto.CallBookingResult;
import it.unicam.hackhub.utils.facade.CalendarEventSpec;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import java.util.UUID;

/** Local-only fake adapter. It never calls Google and must not be enabled in production. */
@Component
@Profile("local")
public class MockCalendarAdapter implements ICalendarAdapter {
    @Override
    public CallBookingResult createMeetEvent(String accessToken, String calendarId, CalendarEventSpec spec) {
        if (spec == null || spec.getStartsAt() == null || spec.getDuration() == null || spec.getDuration().isZero()
                || spec.getDuration().isNegative()) {
            return CallBookingResult.fail("Invalid calendar event details");
        }
        return CallBookingResult.ok("mock-event-" + UUID.randomUUID(), "https://example.invalid/mock-meeting");
    }
}
