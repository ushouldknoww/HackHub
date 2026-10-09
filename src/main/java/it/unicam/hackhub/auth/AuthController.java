package it.unicam.hackhub.auth;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) { this.authService = authService; }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterParticipantRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registerParticipant(request));
    }

    /** Staff provisioning is closed unless a deployment secret is explicitly configured. */
    @PostMapping("/staff/register")
    public ResponseEntity<AuthResponse> registerStaff(
            @RequestHeader(value = "X-Staff-Registration-Key", required = false) String registrationKey,
            @Valid @RequestBody RegisterStaffRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registerStaff(request, registrationKey));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
