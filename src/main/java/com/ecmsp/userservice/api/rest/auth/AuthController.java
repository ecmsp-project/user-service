package com.ecmsp.userservice.api.rest.auth;

import com.ecmsp.userservice.auth.domain.AuthFacade;
import com.ecmsp.userservice.auth.domain.AuthenticationResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthFacade authFacade;

    public AuthController(AuthFacade authFacade) {
        this.authFacade = authFacade;
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthResponse> authenticate(@RequestBody AuthRequest request) {
        AuthenticationResult result = authFacade.authenticate(request.login(), request.password());
        return switch (result) {
            case AuthenticationResult.Success success -> ResponseEntity.ok(new AuthResponse(success.token().value()));
            case AuthenticationResult.Failure ignored -> ResponseEntity.badRequest().build();
        };
    }

}
