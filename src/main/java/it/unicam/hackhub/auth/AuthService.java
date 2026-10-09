package it.unicam.hackhub.auth;

import it.unicam.hackhub.model.StaffProfile;
import it.unicam.hackhub.model.User;
import it.unicam.hackhub.model.valueobjs.Email;
import it.unicam.hackhub.repository.StaffProfileRepository;
import it.unicam.hackhub.repository.UserRepository;
import it.unicam.hackhub.security.ActorPrincipal;
import it.unicam.hackhub.security.TokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

@Service
public class AuthService {
    private final AuthAccountRepository accounts;
    private final UserRepository users;
    private final StaffProfileRepository staffProfiles;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokens;
    private final String staffRegistrationKey;

    public AuthService(AuthAccountRepository accounts, UserRepository users,
                       StaffProfileRepository staffProfiles, PasswordEncoder passwordEncoder,
                       TokenService tokens,
                       @Value("${hackhub.staff-registration-key:}") String staffRegistrationKey) {
        this.accounts = accounts;
        this.users = users;
        this.staffProfiles = staffProfiles;
        this.passwordEncoder = passwordEncoder;
        this.tokens = tokens;
        this.staffRegistrationKey = staffRegistrationKey;
    }

    @Transactional
    public AuthResponse registerParticipant(RegisterParticipantRequest request) {
        String username = normalizeUsername(request.username());
        ensurePasswordFitsBcrypt(request.password());
        ensureUsernameAvailable(username);
        if (users.findByUsername(username) != null) {
            throw new IllegalArgumentException("Username already in use");
        }
        if (users.existsByEmailIgnoreCase(request.email())) {
            throw new IllegalArgumentException("Email already in use");
        }
        User user = users.save(new User(username, request.email(), request.name().trim(), request.surname().trim()));
        AuthAccount account = accounts.save(new AuthAccount(username,
                passwordEncoder.encode(request.password()), AccountRole.PARTICIPANT, user.getId()));
        return response(account);
    }

    @Transactional
    public AuthResponse registerStaff(RegisterStaffRequest request, String providedKey) {
        if (staffRegistrationKey == null || staffRegistrationKey.isBlank()
                || providedKey == null
                || !MessageDigest.isEqual(staffRegistrationKey.getBytes(StandardCharsets.UTF_8),
                                          providedKey.getBytes(StandardCharsets.UTF_8))) {
            throw new AccessDeniedException("Staff registration is disabled or the registration key is invalid");
        }
        ensurePasswordFitsBcrypt(request.password());
        String username = normalizeUsername(request.email());
        ensureUsernameAvailable(username);
        if (staffProfiles.findByEmail(username) != null) {
            throw new IllegalArgumentException("Staff email already in use");
        }
        StaffProfile profile = staffProfiles.save(new StaffProfile(new Email(username),
                request.name().trim(), request.surname().trim()));
        AuthAccount account = accounts.save(new AuthAccount(username,
                passwordEncoder.encode(request.password()), AccountRole.STAFF, profile.getId()));
        return response(account);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        if (request.password().getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new BadCredentialsException("Invalid username or password");
        }
        AuthAccount account = accounts.findByUsernameIgnoreCase(normalizeUsername(request.username()))
                .orElseThrow(() -> new BadCredentialsException("Invalid username or password"));
        if (!passwordEncoder.matches(request.password(), account.getPasswordHash())) {
            throw new BadCredentialsException("Invalid username or password");
        }
        return response(account);
    }

    @Transactional(readOnly = true)
    public ActorProfile profile(ActorPrincipal principal) {
        AuthAccount account = accounts.findById(principal.getAccountId())
                .orElseThrow(() -> new BadCredentialsException("Account no longer exists"));
        if (account.getRole() == AccountRole.PARTICIPANT) {
            User user = users.findById(account.getSubjectId())
                    .orElseThrow(() -> new BadCredentialsException("Participant profile no longer exists"));
            return new ActorProfile(user.getId(), account.getUsername(), account.getRole(), user.getName(), user.getSurname());
        }
        StaffProfile staff = staffProfiles.findById(account.getSubjectId())
                .orElseThrow(() -> new BadCredentialsException("Staff profile no longer exists"));
        return new ActorProfile(staff.getId(), account.getUsername(), account.getRole(), staff.getName(), staff.getSurname());
    }

    private void ensurePasswordFitsBcrypt(String password) {
        // BCrypt considers at most 72 UTF-8 bytes; reject longer values to avoid silent truncation.
        if (password.getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new IllegalArgumentException("Password must not exceed 72 UTF-8 bytes when using BCrypt");
        }
    }

    private void ensureUsernameAvailable(String username) {
        if (accounts.existsByUsernameIgnoreCase(username)) {
            throw new IllegalArgumentException("Username already in use");
        }
    }

    private AuthResponse response(AuthAccount account) {
        return new AuthResponse(tokens.issue(account), "Bearer", tokens.getExpiresInSeconds(),
                account.getRole(), account.getSubjectId());
    }

    private String normalizeUsername(String username) {
        return username.trim().toLowerCase(Locale.ROOT);
    }
}
