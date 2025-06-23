package com.quickmedicalcare.backend.changeInfo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.quickmedicalcare.backend.config.Roles;
import com.quickmedicalcare.backend.config.securityConfig.PasswordEncryptor;
import com.quickmedicalcare.backend.correlationDataDatabase.services.UserDataCorrelationService;
import com.quickmedicalcare.backend.medicalDataDatabase.entities.UserPrivateData;
import com.quickmedicalcare.backend.medicalDataDatabase.services.UserPrivateDataService;
import com.quickmedicalcare.backend.prognosisPackage.SuperTokensPrognosisInterface;
import com.quickmedicalcare.backend.publicDataDatabase.entities.User;
import com.quickmedicalcare.backend.publicDataDatabase.services.UserService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/changeInfo")
public class ChangeInfoController {

    private SuperTokensResetPasswordInterface superTokensAPI;
    private final SuperTokensPrognosisInterface superTokensPrognosis;
    private final UserDataCorrelationService userPatientDataCorrelationService;
    private final UserPrivateDataService userPrivateDataService;
    private PasswordEncryptor passwordEncryptor;
    private UserService userService;

    @GetMapping("/getUserDetails")
    public ResponseEntity<?> getUserDetails() throws JsonProcessingException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByUserIdSuperTokens(auth.getName());
        if (user == null) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
        long privateDataId = userPatientDataCorrelationService.getUserPrivateDataId(user.getId());
        UserPrivateData userPrivateData = userPrivateDataService.getPatientById(privateDataId);
        if (userPrivateData == null) {
            return new ResponseEntity<>("Private data not found", HttpStatus.NOT_FOUND);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("User general data", user);
        result.put("Private data", userPrivateData);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/getRole")
    public ResponseEntity<?> getUserRole() throws IOException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByUserIdSuperTokens(auth.getName());
        if (user == null) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
        List<Roles> userRoles = superTokensPrognosis.getUserRoles(SecurityContextHolder
                .getContext().getAuthentication().getName());
        List<Roles> mutableRoles = new ArrayList<>(userRoles);
        mutableRoles.sort(Comparator.comparingInt(Roles::getPriority));
        Roles userRole = mutableRoles.get(userRoles.size() - 1);
        return ResponseEntity.ok(userRole);
    }

    @PutMapping("/changePassword")
    @Transactional
    public ResponseEntity<String> changePassword(@RequestBody ChangeInfoPayload changeInfoPayload) throws IOException {
        if (changeInfoPayload.getPassword() == null || changeInfoPayload.getPassword().isEmpty() ||
        changeInfoPayload.getOldPassword() == null || changeInfoPayload.getOldPassword().isEmpty()){
            return new ResponseEntity<>("New or old password not provided", HttpStatus.BAD_REQUEST);
        }

        changeInfoPayload.setPassword(passwordEncryptor.encryptPassword(changeInfoPayload.getPassword()));
        changeInfoPayload.setOldPassword(passwordEncryptor.encryptPassword(changeInfoPayload.getOldPassword()));

        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findUserByUserIdSuperTokens(userId);
        if (user == null) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
        String resetPasswordToken = superTokensAPI.getPasswordToken(userId, user.getEmail());
        if (resetPasswordToken == null || resetPasswordToken.isEmpty()) {
            return new ResponseEntity<>("Reset password token not provided", HttpStatus.BAD_REQUEST);
        }
        Boolean changed = superTokensAPI.resetPassword(resetPasswordToken, changeInfoPayload.getPassword());
        if (changed) {
            if (userService.changeUserPassword(changeInfoPayload.getPassword(), user)) {
                return new ResponseEntity<>("Password changed", HttpStatus.OK);
            }
        }
        return new ResponseEntity<>("Could not change password", HttpStatus.BAD_REQUEST);
    }

    @PutMapping("/changeCredentials")
    @Transactional
    public ResponseEntity<String> changeCredentials(@RequestBody ChangeInfoPayload changeInfoPayload) throws IOException {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findUserByUserIdSuperTokens(userId);
        if (user == null) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
        if (changeInfoPayload.getEmail() != null && !changeInfoPayload.getEmail().isEmpty()) {
            Boolean emailChanged = superTokensAPI.resetEmail(changeInfoPayload.getEmail(), userId, user.getPassword());
            if (!emailChanged) {
                return new ResponseEntity<>("Email could not be changed", HttpStatus.BAD_REQUEST);
            }
        }
        Boolean changedData = userService.changeCredentials(changeInfoPayload, user);
        if (!changedData) {
            superTokensAPI.resetEmail(user.getEmail(), userId, user.getPassword());
            return new ResponseEntity<>("Could not change credentials", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Credentials changed", HttpStatus.OK);
    }
}
