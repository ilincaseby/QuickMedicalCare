package com.quickmedicalcare.backend.registerLogin;

import com.quickmedicalcare.backend.config.Roles;
import com.quickmedicalcare.backend.config.securityConfig.PasswordEncryptor;
import com.quickmedicalcare.backend.correlationDataDatabase.services.UserDataCorrelationService;
import com.quickmedicalcare.backend.medicalDataDatabase.services.UserPrivateDataService;
import com.quickmedicalcare.backend.prognosisPackage.SuperTokensPrognosisInterface;
import com.quickmedicalcare.backend.publicDataDatabase.services.UserService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/authentication")
public class AuthenticationServiceController {

    private SuperTokensInterface superTokensAPI;
    private UserService userService;
    private UserPrivateDataService patientDataService;
    private UserDataCorrelationService userPatientDataCorrelationService;
    private PasswordEncryptor passwordEncryptor;
    private final SuperTokensPrognosisInterface roleGetter;

    @PostMapping("/register")
    @Transactional
    public ResponseEntity<?> registerUser(@RequestBody RegisterPayload registerPayload) throws IOException {
        if (userService.userExists(registerPayload.getUsername(), registerPayload.getEmail())) {
            return new ResponseEntity<>("Username or email already in use", HttpStatus.BAD_REQUEST);
        }
        registerPayload.setPassword(passwordEncryptor.encryptPassword(registerPayload.getPassword()));
        SuperTokensUtilityClass.RegisterAnswerClass regAns = superTokensAPI.register(registerPayload.getEmail(), registerPayload.getPassword());
        if (regAns.getStatusCode() != HttpStatus.CREATED) {
            return new ResponseEntity<>("User could not be created", regAns.getStatusCode());
        }
        saveUserGDPR(registerPayload, regAns);
        HttpHeaders headers = new HttpHeaders();
        return new ResponseEntity<>("User registered", headers, regAns.getStatusCode());
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginPayload loginPayload) throws IOException {
        loginPayload.setPassword(passwordEncryptor.encryptPassword(loginPayload.getPassword()));
        SuperTokensUtilityClass.TokenCodeClass tokenCodeClass = superTokensAPI.login(loginPayload.getEmail(), loginPayload.getPassword());
        if (tokenCodeClass.getCode() == HttpStatus.BAD_REQUEST.value()) {
            return new ResponseEntity<>("Wrong credentials", HttpStatus.BAD_REQUEST);
        }
        if (tokenCodeClass.getCode() != HttpStatus.OK.value()) {
            return new ResponseEntity<>("Server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        SuperTokensUtilityClass.TokenClass tokens = superTokensAPI.getToken(tokenCodeClass.getUser_id());
//        HttpHeaders headers = addCookies(Map.of("access-token", tokens.getAccessToken(),
//                "refresh-token", tokens.getRefreshToken(), "antiCsrfToken",
//                tokens.getRefreshToken()), tokens.getExpiresIn());
        List<Roles> userRoles = roleGetter.getUserRoles(tokenCodeClass.getUser_id());
        userRoles.sort(Comparator.comparingInt(Roles::getPriority));
        if (userRoles.isEmpty()) {
            return new ResponseEntity<>("User role not found", HttpStatus.NOT_FOUND);
        }
        Roles userRole = userRoles.get(userRoles.size() - 1);

        return ResponseEntity.ok().body(Map.of("access-token", tokens.getAccessToken(),
                "refresh-token", tokens.getRefreshToken(), "antiCsrfToken",
                tokens.getRefreshToken(), "role", userRole.toString()));
//        return new ResponseEntity<>("User logged in", headers, HttpStatus.OK);
    }

    @PutMapping("/upgrade")
    public ResponseEntity<?> upgrade(@RequestParam String upgradeRole) throws IOException {
        List<Roles> userRoles = roleGetter.getUserRoles(SecurityContextHolder
                .getContext().getAuthentication().getName());
        Roles desiredRole = Roles.fromString(upgradeRole);
        if (desiredRole == null) {
            return new ResponseEntity<>("Invalid role", HttpStatus.BAD_REQUEST);
        }
        if (userRoles.contains(desiredRole)) {
            return ResponseEntity.status(409).body("Specified role already exists for user");
        }
        HttpStatus setRoleCode = superTokensAPI.setRole(SecurityContextHolder.getContext().getAuthentication().getName(),
                upgradeRole);
        if (setRoleCode == HttpStatus.OK) {
            return ResponseEntity.status(200).body("Role upgraded successfully");
        }
        return ResponseEntity.status(setRoleCode).body("Role upgraded failed in superTokens");
    }

    private HttpHeaders addCookies(Map<String, String> cookies, Long expiresIn) {
        HttpHeaders headers = new HttpHeaders();
        for (Map.Entry<String, String> entry : cookies.entrySet()) {
            ResponseCookie cookie = ResponseCookie.from(entry.getKey(), entry.getValue())
                    .httpOnly(true)
                    .maxAge(expiresIn)
                    .build();
            headers.add("Set-Cookie", cookie.toString());
        }
        return headers;
    }

    private void saveUserGDPR(RegisterPayload registerPayload, SuperTokensUtilityClass.RegisterAnswerClass regAns) throws IOException {
        Long userId = userService.saveUser(registerPayload, regAns.getUserId());
        Long patientId = patientDataService.savePatient(registerPayload);
        userPatientDataCorrelationService.saveUserPatientDataCorrelation(userId, patientId);
        superTokensAPI.setRole(regAns.getUserId(), Roles.ROLE_USER_BASIC.toString());
    }
}
