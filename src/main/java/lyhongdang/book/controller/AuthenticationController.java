package lyhongdang.book.controller;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.SignedJWT;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lyhongdang.book.dto.request.*;
import lyhongdang.book.dto.request.response.IntrospectResponse;
import lyhongdang.book.dto.request.response.LoginResponse;
import lyhongdang.book.entity.User;
import lyhongdang.book.service.AuthenticationService;
import lyhongdang.book.service.JwtService;
import lyhongdang.book.service.UserService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication API")
public class AuthenticationController {

    private final AuthenticationService service;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;

    /* =======================================================
     *                REGISTER & ACTIVATION / LOCK USER
     * ======================================================= */

    @PostMapping("/register")
    @Operation(summary = "Register a new account", description = "Register a new account and send activation email.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registration successful"),
            @ApiResponse(responseCode = "400", description = "Invalid email format or password too short")
    })
    public ResponseEntity<String> register(@RequestBody @Valid RegistrationRequest request) throws MessagingException {
        service.register(request);
        return ResponseEntity.ok("Registration successful");
    }

    @GetMapping("/activate-account")
    @Operation(summary = "Activate account", description = "Activate user account using the token sent to email.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account activated successfully"),
            @ApiResponse(responseCode = "404", description = "Token or user not found", content = @Content()),
            @ApiResponse(responseCode = "400", description = "Token expired",content = @Content())
    })
    public ResponseEntity<String> activateAccount(
            @Parameter(description = "Activation token", example = "123456")
            @RequestParam String token) throws MessagingException {
        service.activateAccount(token);
        return ResponseEntity.ok("Account activated");
    }

    @PostMapping("/reload-token")
    @Operation(summary = "Reload activation token", description = "Resend a new activation token by email.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "New token sent successfully"),
            @ApiResponse(responseCode = "404", description = "User not found",content = @Content()),
            @ApiResponse(responseCode = "500", description = "Error while sending email",content = @Content())
    })
    public ResponseEntity<String> reloadToken(
            @Parameter(description = "Email of user", example = "lyhongdang03@gmail.com")
            @RequestBody String email) {
        try {
            service.reLoadToken(email);
            return ResponseEntity.ok("New token has been sent to email: " + email);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (MessagingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error while sending validation email");
        }
    }

    @PatchMapping("/lock-user")
    @Operation(summary = "Lock user", description = "Lock user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully"),
            @ApiResponse(responseCode = "404", description = "User not found",content = @Content()),
            @ApiResponse(responseCode = "500", description = "Error while sending email",content = @Content())
    })
    public ResponseEntity<String> lockUser(@RequestBody LockUserRequest request){
        return ResponseEntity.ok(userService.lockUser(request));
    }

    /* =======================================================
     *                LOGIN / LOGOUT / REFRESH
     * ======================================================= */

    @PostMapping("/login")
    @Operation(summary = "Login", description = "Authenticate user with email and password.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful",
                    content = @Content(schema = @Schema(implementation = LoginResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid credentials or invalid email/password format", content = @Content),
    })
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request) throws JOSEException {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtService.generateToken(request.getEmail());
        String refreshToken = jwtService.generateRefreshToken(request.getEmail());

        userService.updateUserToken(refreshToken, request.getEmail());

        ResponseCookie cookie = ResponseCookie.from("RefreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(604800) // 7 days
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new LoginResponse(token));
    }

    @GetMapping("/refresh-token")
    @Operation(summary = "Refresh access token", description = "Generate a new access token using a valid refresh token.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token refreshed successfully",
                    content = @Content(schema = @Schema(implementation = LoginResponse.class))),
            @ApiResponse(responseCode = "401", description = "Invalid or expired refresh token", content = @Content)
    })
    public ResponseEntity<LoginResponse> refresh(
            @Parameter(description = "Refresh token stored in cookie")
            @CookieValue(name = "RefreshToken") String refreshToken) throws Exception {

        var request = new IntrospectRequest(refreshToken);
        var checkValid = jwtService.isTokenValidRefreshToken(request);
        if (!checkValid.isValid()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        SignedJWT signedJWT = SignedJWT.parse(request.getToken());
        String email = signedJWT.getJWTClaimsSet().getSubject();

        User user = userService.getUserRefreshTokenAndEmail(request.getToken(), email);

        String token = jwtService.generateToken(user.getEmail());
        String newRefreshToken = jwtService.generateRefreshToken(user.getEmail());
        userService.updateUserToken(newRefreshToken, user.getEmail());

        ResponseCookie cookie = ResponseCookie.from("RefreshToken", newRefreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(signedJWT.getJWTClaimsSet().getExpirationTime().toInstant().toEpochMilli())
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new LoginResponse(token));
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout", description = "Logout current user and clear refresh token.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Logout successful")
    })
    public ResponseEntity<String> logout() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        userService.updateUserToken(null, email);

        ResponseCookie cookie = ResponseCookie.from("RefreshToken", null)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body("Logout successful");
    }

    /* =======================================================
     *                PASSWORD FLOW
     * ======================================================= */

    @PostMapping("/forgot-password")
    @Operation(summary = "Forgot password", description = "Send password reset instructions to user's email.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Password reset email sent", content = @Content()),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content()),
    })
    public ResponseEntity<Void> forgotPassword(@RequestBody @Valid ForgotPasswordRequest email) throws MessagingException {
        service.forgotAccount(email);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Reset password", description = "Reset user password with token and new password.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Password reset successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid or expired token", content = @Content())
    })
    public ResponseEntity<Void> resetPassword(@RequestBody @Valid ResetPasswordRequest request) throws MessagingException {
        service.resetPassword(request);
        return ResponseEntity.accepted().build();
    }

    /* =======================================================
     *                TOKEN UTILITIES
     * ======================================================= */

    @PostMapping("/verify-token")
    @Operation(summary = "Verify token", description = "Check whether a token is valid and not expired.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Token verification result",
                    content = @Content(schema = @Schema(implementation = IntrospectResponse.class)))
    })
    public ResponseEntity<IntrospectResponse> verify(@RequestBody IntrospectRequest request) throws Exception {
        var result = jwtService.isTokenValid(request);
        return ResponseEntity.accepted().body(result);
    }
}
