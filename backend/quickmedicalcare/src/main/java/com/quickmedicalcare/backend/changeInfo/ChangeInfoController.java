package com.quickmedicalcare.backend.changeInfo;

import com.quickmedicalcare.backend.config.securityConfig.PasswordEncryptor;
import com.quickmedicalcare.backend.publicDataDatabase.entities.User;
import com.quickmedicalcare.backend.publicDataDatabase.services.UserService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/changeInfo")
public class ChangeInfoController {

    private SuperTokensResetPasswordInterface superTokensAPI;
    private PasswordEncryptor passwordEncryptor;
    private UserService userService;

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
