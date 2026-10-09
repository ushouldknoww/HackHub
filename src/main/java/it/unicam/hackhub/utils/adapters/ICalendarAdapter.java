package it.unicam.hackhub.utils.adapters;

import it.unicam.hackhub.model.dto.requestdto.CallBookingResult;
import it.unicam.hackhub.utils.facade.CalendarEventSpec;

public interface ICalendarAdapter {
    CallBookingResult createMeetEvent(String accessToken, String calendarId, CalendarEventSpec spec);
}