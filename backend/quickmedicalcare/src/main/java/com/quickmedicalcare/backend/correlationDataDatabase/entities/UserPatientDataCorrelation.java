package com.quickmedicalcare.backend.correlationDataDatabase.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_data_correlation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserPatientDataCorrelation {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "patient_data_id")
    private Long patientDataId;

    public UserPatientDataCorrelation(Long userId, Long patientDataId) {
        this.patientDataId = patientDataId;
        this.userId = userId;
    }
}
