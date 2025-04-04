package com.quickmedicalcare.backend.registerLogin;

import com.quickmedicalcare.backend.config.Constants;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RegisterPayload {
    @NotNull
    private String username;
    @NotNull
    private String firstName;
    @NotNull
    private String lastName;
    @NotNull
    @Email
    private String email;
    @Size(min = Constants.MIN_LENGTH_PASSWORD, max = Constants.MAX_LENGTH_PASSWORD, message = "The password must have between " + Constants.MIN_LENGTH_PASSWORD + " and " + Constants.MAX_LENGTH_PASSWORD + " character")
    private String password;
    @NotNull
    private int age;
    @NotNull
    private double weight;
    @NotNull
    private double height;
    @NotNull
    private int sex;
    @NotNull
    private String country;
    @NotNull
    private boolean smoker;
    @NotNull
    private int alcoholConsumptionFreq;

}
