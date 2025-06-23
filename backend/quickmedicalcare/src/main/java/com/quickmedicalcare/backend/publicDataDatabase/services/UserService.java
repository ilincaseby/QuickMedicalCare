package com.quickmedicalcare.backend.publicDataDatabase.services;

import com.quickmedicalcare.backend.changeInfo.ChangeInfoPayload;
import com.quickmedicalcare.backend.correlationDataDatabase.services.UserDataCorrelationService;
import com.quickmedicalcare.backend.registerLogin.RegisterPayload;
import com.quickmedicalcare.backend.publicDataDatabase.entities.User;
import com.quickmedicalcare.backend.publicDataDatabase.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserDataCorrelationService userPatientDataCorrelationService;

    public boolean userExists(String username, String email) {
        return userRepository.existsByEmail(email) || userRepository.existsByUsername(username);
    }

    public Long saveUser(RegisterPayload registerPayload, String userIdSuperTokens) {
        return userRepository.save(new User(registerPayload.getUsername(),
                registerPayload.getFirstName(), registerPayload.getLastName(),
                registerPayload.getEmail(), registerPayload.getPassword(), userIdSuperTokens)).getId();
    }

    public User findUserByUserIdSuperTokens(String userIdSuperTokens) {
        return userRepository.findByUserIdSuperTokens(userIdSuperTokens).orElse(null);
    }

    public Boolean changeUserPassword(String newPassword, User user) {
        user.setPassword(newPassword);
        return userRepository.save(user) != null;
    }

    public Boolean changeCredentials(ChangeInfoPayload changeInfoPayload, User user) {
        if (!userPatientDataCorrelationService.changeCredentialsById(user.getId(), changeInfoPayload)) {
            return false;
        }
        if (changeInfoPayload.getUsername() != null) {
            user.setUsername(changeInfoPayload.getUsername());
        }
        if (changeInfoPayload.getFirstName() != null) {
            user.setFirstName(changeInfoPayload.getFirstName());
        }
        if (changeInfoPayload.getLastName() != null) {
            user.setLastName(changeInfoPayload.getLastName());
        }
        if (changeInfoPayload.getEmail() != null) {
            user.setEmail(changeInfoPayload.getEmail());
        }
        return userRepository.save(user) != null;
    }
}
