package it.unicam.hackhub.notifications;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
public class EmailNotificationService {
    private static final Logger log = LoggerFactory.getLogger(EmailNotificationService.class);
    private final ObjectProvider<JavaMailSender> mailSenderProvider;
    private final String fromAddress;

    public EmailNotificationService(ObjectProvider<JavaMailSender> mailSenderProvider,
                                    @Value("${hackhub.notifications.from:}") String fromAddress) {
        this.mailSenderProvider = mailSenderProvider;
        this.fromAddress = fromAddress;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onInvitationCreated(InvitationCreatedEvent event) {
        JavaMailSender sender = mailSenderProvider.getIfAvailable();
        if (sender == null || fromAddress == null || fromAddress.isBlank()) {
            log.info("Email notification not sent because SMTP is not configured (event=team-invitation, invitationId={})",
                    event.invitationId());
            return;
        }
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromAddress);
            message.setTo(event.recipientEmail());
            message.setSubject("You've been invited to join a HackHub team");
            message.setText("Hello " + event.inviteeName() + ",\n\n" + event.inviterName()
                    + " invited you to join team \"" + event.teamName() + "\" on HackHub.\n"
                    + "Sign in to HackHub to review your invitation (reference: " + event.invitationId() + ").\n\n"
                    + "If you were not expecting this invitation, you can ignore this message.");
            sender.send(message);
        } catch (RuntimeException exception) {
            // The invitation is already committed; email delivery failure must not roll back the operation.
            log.error("Could not send invitation email for invitation {}", event.invitationId(), exception);
        }
    }
}
