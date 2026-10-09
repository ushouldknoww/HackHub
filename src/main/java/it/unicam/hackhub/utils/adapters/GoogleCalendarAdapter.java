package it.unicam.hackhub.utils.adapters;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.*;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;

import it.unicam.hackhub.model.dto.requestdto.CallBookingResult;
import it.unicam.hackhub.utils.facade.CalendarEventSpec;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Profile;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.UUID;

@Component
@Profile("postgres")
public class GoogleCalendarAdapter implements ICalendarAdapter {

    private static final String APPLICATION_NAME = "Hackathon Manager";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    @Override
    public CallBookingResult createMeetEvent(String accessToken, String calendarId, CalendarEventSpec spec) {
        if (accessToken == null || accessToken.isBlank() || spec == null
                || spec.getStartsAt() == null || spec.getDuration() == null
                || spec.getDuration().isNegative() || spec.getDuration().isZero()) {
            return CallBookingResult.fail("Token Google o dettagli dell'evento mancanti/non validi.");
        }
        try {
            NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            AccessToken authAccessToken = new AccessToken(accessToken, null);
            GoogleCredentials credentials = GoogleCredentials.create(authAccessToken);
            HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(credentials);
            Calendar service = new Calendar.Builder(httpTransport, JSON_FACTORY, requestInitializer)
                    .setApplicationName(APPLICATION_NAME)
                    .build();

            ZoneId zone = ZoneId.of("Europe/Rome");
            ZonedDateTime start = spec.getStartsAt().atZone(zone);
            ZonedDateTime end = spec.getStartsAt().plus(spec.getDuration()).atZone(zone);
            Event event = new Event()
                    .setSummary(spec.getTitle())
                    .setDescription(spec.getDescription())
                    .setStart(new EventDateTime()
                            .setDateTime(new DateTime(start.toInstant().toEpochMilli()))
                            .setTimeZone(zone.getId()))
                    .setEnd(new EventDateTime()
                            .setDateTime(new DateTime(end.toInstant().toEpochMilli()))
                            .setTimeZone(zone.getId()));

            if (spec.getInviteeEmail() != null && !spec.getInviteeEmail().isBlank()) {
                event.setAttendees(Collections.singletonList(new EventAttendee().setEmail(spec.getInviteeEmail())));
            }
            event.setConferenceData(new ConferenceData().setCreateRequest(
                    new CreateConferenceRequest()
                            .setRequestId(UUID.randomUUID().toString())
                            .setConferenceSolutionKey(new ConferenceSolutionKey().setType("hangoutsMeet"))));

            Event created = service.events().insert(calendarId, event)
                    .setConferenceDataVersion(1)
                    .execute();
            String meetingUrl = created.getHangoutLink();
            if ((meetingUrl == null || meetingUrl.isBlank()) && created.getConferenceData() != null
                    && created.getConferenceData().getEntryPoints() != null) {
                meetingUrl = created.getConferenceData().getEntryPoints().stream()
                        .filter(point -> "video".equalsIgnoreCase(point.getEntryPointType()))
                        .map(EntryPoint::getUri)
                        .filter(uri -> uri != null && !uri.isBlank())
                        .findFirst().orElse(null);
            }
            return CallBookingResult.ok(created.getId(), meetingUrl);
        } catch (Exception exception) {
            return CallBookingResult.fail("Google Calendar request failed: " + exception.getMessage());
        }
    }
}