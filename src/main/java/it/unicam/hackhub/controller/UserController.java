package it.unicam.hackhub.controller;

import it.unicam.hackhub.auth.ActorProfile;
import it.unicam.hackhub.auth.AuthService;
import it.unicam.hackhub.security.ActorPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final AuthService authService;
    public UserController(AuthService authService) { this.authService = authService; }

    @GetMapping("/me")
    public ActorProfile me(@AuthenticationPrincipal ActorPrincipal principal) {
        return authService.profile(principal);
    }
}
