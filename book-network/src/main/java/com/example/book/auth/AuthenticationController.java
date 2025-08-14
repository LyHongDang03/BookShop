package com.example.book.auth;

import com.example.book.config.JwtService;
import com.example.book.user.User;
import com.example.book.user.UserService;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.SignedJWT;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService service;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<?> register(@RequestBody @Valid RegistrationRequest request) throws MessagingException {
        service.register(request);
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/activate-account")
    public ResponseEntity<?> activateAccount(@RequestParam String token) throws MessagingException {
        service.activateAccount(token);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request) throws JOSEException {
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword());
        Authentication authentication = authenticationManager.authenticate(auth);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtService.generateToken(request.getEmail());
        System.out.println("Token: "+token);
        String refreshToken = jwtService.generateRefreshToken(request.getEmail());
        userService.updateUserToken(refreshToken, request.getEmail());
        ResponseCookie responseCookie = ResponseCookie.from("RefreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(604800000)
                .build();

        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .body(new LoginResponse(token));
    }
    @PostMapping("/verify-token")
    public ResponseEntity<IntrospectResponse> verify (@RequestBody IntrospectRequest request) throws Exception {
        var result = jwtService.isTokenValid(request);
        return ResponseEntity.accepted().body(result);
    }

    @GetMapping("/refresh-token")
    public ResponseEntity<LoginResponse> refresh(@CookieValue(name = "RefreshToken") IntrospectRequest request) throws Exception {
        var checkValid = jwtService.isTokenValidRefreshToken(request);
        if (checkValid.isValid()) {
            SignedJWT signedJWT = SignedJWT.parse(request.getToken());
            var email = signedJWT.getJWTClaimsSet().getSubject();
            User user = userService.getUserRefreshTokenAndEmail(request.getToken(), email);
            String token = jwtService.generateToken(user.getEmail());
            String new_refreshToken = jwtService.generateRefreshToken(user.getEmail());
            userService.updateUserToken(new_refreshToken, user.getEmail());
            ResponseCookie responseCookie = ResponseCookie.from("RefreshToken", new_refreshToken)
                    .httpOnly(true)
                    .secure(true)
                    .path("/")
                    .maxAge(signedJWT.getJWTClaimsSet().getExpirationTime().toInstant().toEpochMilli())
                    .build();

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                    .body(new LoginResponse(token));
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(){
        var email = SecurityContextHolder.getContext().getAuthentication().getName();
        userService.updateUserToken(null, email);
        ResponseCookie responseCookie = ResponseCookie.from("RefreshToken",null)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .body("Logout");
    }
    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(@RequestBody ForgotPasswordRequest email) throws MessagingException {
        service.forgotAccount(email);
        return ResponseEntity.accepted().build();
    }
    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@RequestBody ResetPasswordRequest request) throws MessagingException {
        service.resetPassword(request);
        return ResponseEntity.accepted().build();
    }
}
