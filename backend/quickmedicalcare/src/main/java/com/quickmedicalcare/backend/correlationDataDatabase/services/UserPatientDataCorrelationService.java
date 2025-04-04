package com.quickmedicalcare.backend.correlationDataDatabase.services;

import com.quickmedicalcare.backend.changeInfo.ChangeInfoPayload;
import com.quickmedicalcare.backend.correlationDataDatabase.entities.UserPatientDataCorrelation;
import com.quickmedicalcare.backend.correlationDataDatabase.repositories.UserPatientDataCorrelationRepository;
import com.quickmedicalcare.backend.medicalDataDatabase.services.PatientDataService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Getter
@Setter
@AllArgsConstructor
@Service
public class UserPatientDataCorrelationService {
    private final UserPatientDataCorrelationRepository userPatientDataCorrelationRepository;
    private final PatientDataService patientDataService;

    public void saveUserPatientDataCorrelation(Long userId, Long patientId) {
        userPatientDataCorrelationRepository.save(new UserPatientDataCorrelation(userId, patientId));
    }

    public Boolean changeCredentialsById(Long userId, ChangeInfoPayload changeInfoPayload) {
        Optional<UserPatientDataCorrelation> userPatientDataCorrelation =
                userPatientDataCorrelationRepository
                        .findUserPatientDataCorrelationByUserId(userId);
        return (userPatientDataCorrelation.isPresent()) ? patientDataService
                .changeCredentials(changeInfoPayload, userPatientDataCorrelation
                        .get().getPatientDataId()) : false;
    }
}
