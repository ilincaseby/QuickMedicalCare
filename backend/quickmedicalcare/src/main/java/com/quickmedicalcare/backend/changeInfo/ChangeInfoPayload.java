package com.quickmedicalcare.backend.changeInfo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChangeInfoPayload {
    private String username;
    private String oldPassword;
    private String password;
    private String firstName;
    private String lastName;
    private String email;
    private Integer age;
    private Double weight;
    private Double height;
    private Integer sex;
    private String country;
    private Boolean smoker;
    private Integer alcoholConsumptionFreq;
}
