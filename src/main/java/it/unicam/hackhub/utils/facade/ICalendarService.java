package it.unicam.hackhub.utils.facade;

import it.unicam.hackhub.model.dto.CallBookingRequest;
import it.unicam.hackhub.model.dto.requestdto.CallBookingResult;

public interface ICalendarService {
    CallBookingResult scheduleCall(CallBookingRequest request);
}