package com.quickmedicalcare.backend.publicDataDatabase.services;

import com.quickmedicalcare.backend.RegisterLogin.RegisterPayload;
import com.quickmedicalcare.backend.publicDataDatabase.entities.User;
import com.quickmedicalcare.backend.publicDataDatabase.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    public boolean userExists(String username, String email) {
        return userRepository.existsByEmail(email) || userRepository.existsByUsername(username);
    }

    public Long saveUser(RegisterPayload registerPayload, String userIdSuperTokens) {
        return userRepository.save(new User(registerPayload.getUsername(),
                registerPayload.getFirstName(), registerPayload.getLastName(),
                registerPayload.getEmail(), registerPayload.getPassword(), userIdSuperTokens)).getId();
    }
}
