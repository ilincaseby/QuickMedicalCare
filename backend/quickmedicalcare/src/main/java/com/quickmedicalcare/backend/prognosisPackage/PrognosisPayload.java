package com.quickmedicalcare.backend.prognosisPackage;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PrognosisPayload {
    private String message;
    private int age;
    private String sex;
}
