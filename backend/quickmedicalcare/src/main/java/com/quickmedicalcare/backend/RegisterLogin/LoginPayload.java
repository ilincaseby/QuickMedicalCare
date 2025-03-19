package com.quickmedicalcare.backend.RegisterLogin;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LoginPayload {
    private String username;
    @Email
    private String email;
    private String password;
}
