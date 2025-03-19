package com.quickmedicalcare.backend.RegisterLogin;

import com.quickmedicalcare.backend.config.securityConfig.PasswordEncryptor;
import com.quickmedicalcare.backend.correlationDataDatabase.services.UserPatientDataCorrelationService;
import com.quickmedicalcare.backend.medicalDataDatabase.services.PatientDataService;
import com.quickmedicalcare.backend.publicDataDatabase.services.UserService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.time.Duration;

@AllArgsConstructor
@Controller
@RequestMapping("/api/v1/authentication")
public class AuthenticationServiceController {

    private SuperTokensInterface superTokensAPI;
    private UserService userService;
    private PatientDataService patientDataService;
    private UserPatientDataCorrelationService userPatientDataCorrelationService;
    private PasswordEncryptor passwordEncryptor;

    @PostMapping("/register")
    @Transactional
    public ResponseEntity<String> registerUser(@RequestBody RegisterPayload registerPayload) throws IOException {
        if (userService.userExists(registerPayload.getUsername(), registerPayload.getEmail())) {
            return new ResponseEntity<>("Username or email already in use", HttpStatus.BAD_REQUEST);
        }
        registerPayload.setPassword(passwordEncryptor.encryptPassword(registerPayload.getPassword()));
        SuperTokensUtilityClass.RegisterAnswerClass regAns = superTokensAPI.register(registerPayload.getEmail(), registerPayload.getPassword());
        if (regAns.getStatusCode() != HttpStatus.CREATED) {
            return new ResponseEntity<>("User could not be created", regAns.getStatusCode());
        }
        Long userId = userService.saveUser(registerPayload, regAns.getUserId());
        Long patientId = patientDataService.savePatient(registerPayload);
        userPatientDataCorrelationService.saveUserPatientDataCorrelation(userId, patientId);
        HttpHeaders headers = new HttpHeaders();
        return new ResponseEntity<>("User registered", headers, regAns.getStatusCode());
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody LoginPayload loginPayload) throws IOException {
        loginPayload.setPassword(passwordEncryptor.encryptPassword(loginPayload.getPassword()));
        SuperTokensUtilityClass.TokenCodeClass tokenCodeClass = superTokensAPI.login(loginPayload.getEmail(), loginPayload.getPassword());
        if (tokenCodeClass.getCode() != HttpStatus.OK.value()) {
            return new ResponseEntity<>("Login failed", HttpStatus.valueOf(tokenCodeClass.getCode()));
        }
        SuperTokensUtilityClass.TokenClass tokens = superTokensAPI.getToken(tokenCodeClass.getUser_id());
        ResponseCookie accessTokenCookie = ResponseCookie.from("access-token", tokens.getAccessToken())
                .httpOnly(true)
                .maxAge(Duration.ofMillis(tokens.getExpiresIn()))
                .build();
        ResponseCookie refreshTokenCookie = ResponseCookie.from("refresh-token", tokens.getRefreshToken())
                .httpOnly(true)
                .maxAge(Duration.ofMillis(tokens.getExpiresIn()))
                .build();
        ResponseCookie antiCsrfTokenCookie = ResponseCookie.from("antiCsrfToken", tokens.getRefreshToken())
                .httpOnly(true)
                .maxAge(Duration.ofMillis(tokens.getExpiresIn()))
                .build();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Set-Cookie", accessTokenCookie.toString());
        headers.add("Set-Cookie", refreshTokenCookie.toString());
        headers.add("Set-Cookie", antiCsrfTokenCookie.toString());
        return new ResponseEntity<>("User logged in", headers, HttpStatus.OK);
    }
}
