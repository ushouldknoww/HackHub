package it.unicam.hackhub;

import it.unicam.hackhub.auth.AccountRole;
import it.unicam.hackhub.auth.AuthAccount;
import it.unicam.hackhub.security.ActorPrincipal;
import it.unicam.hackhub.security.TokenService;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

class TokenServiceTest {
    private static final String SECRET = "unit-test-secret-that-is-at-least-32-bytes-long";

    @Test
    void signedTokenRoundTripsPrincipalClaims() throws Exception {
        AuthAccount account = new AuthAccount("alice", "$2a$12$test-hash", AccountRole.PARTICIPANT, 42L);
        Field id = AuthAccount.class.getDeclaredField("id");
        id.setAccessible(true);
        id.set(account, 7L);

        TokenService service = new TokenService(SECRET, 3600);
        ActorPrincipal actor = service.parse(service.issue(account));

        assertEquals(7L, actor.getAccountId());
        assertEquals(42L, actor.getSubjectId());
        assertEquals("alice", actor.getUsername());
        assertEquals(AccountRole.PARTICIPANT, actor.getRole());
    }

    @Test
    void rejectsWeakSigningSecret() {
        assertThrows(IllegalArgumentException.class, () -> new TokenService("short", 3600));
    }

    @Test
    void rejectsUnsafeExpiration() {
        assertThrows(IllegalArgumentException.class, () -> new TokenService(SECRET, 10));
    }
}
