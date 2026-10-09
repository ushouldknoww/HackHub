package it.unicam.hackhub.utils.facade;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/** Production-safe placeholder: never returns a fabricated bearer token. */
@Service
@Profile("postgres")
public class UnconfiguredGoogleOAuthTokenStore implements GoogleOAuthTokenStore {
    @Override
    public String findAccessTokenByStaffProfileId(Long staffProfileId) {
        // Wire an encrypted, per-staff OAuth token store before enabling Calendar in production.
        return null;
    }
}
