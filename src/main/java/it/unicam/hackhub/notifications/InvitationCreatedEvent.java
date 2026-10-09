package it.unicam.hackhub.notifications;

public record InvitationCreatedEvent(String recipientEmail, String inviteeName,
                                     String inviterName, String teamName, Long invitationId) { }
