package com.quickmedicalcare.backend.correlationDataDatabase.services;

import com.quickmedicalcare.backend.changeInfo.ChangeInfoPayload;
import com.quickmedicalcare.backend.correlationDataDatabase.entities.UserDataCorrelation;
import com.quickmedicalcare.backend.correlationDataDatabase.repositories.UserDataCorrelationRepository;
import com.quickmedicalcare.backend.medicalDataDatabase.services.UserPrivateDataService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Getter
@Setter
@AllArgsConstructor
@Service
public class UserDataCorrelationService {
    private final UserDataCorrelationRepository userPatientDataCorrelationRepository;
    private final UserPrivateDataService patientDataService;

    public void saveUserPatientDataCorrelation(Long userId, Long patientId) {
        userPatientDataCorrelationRepository.save(new UserDataCorrelation(userId, patientId));
    }

    public Boolean changeCredentialsById(Long userId, ChangeInfoPayload changeInfoPayload) {
        Optional<UserDataCorrelation> userPatientDataCorrelation =
                userPatientDataCorrelationRepository
                        .findUserPatientDataCorrelationByUserId(userId);
        return (userPatientDataCorrelation.isPresent()) ? patientDataService
                .changeCredentials(changeInfoPayload, userPatientDataCorrelation
                        .get().getUserPrivateDataId()) : false;
    }

    public long getUserPrivateDataId(Long userId) {
        UserDataCorrelation userPatientDataCorrelation = userPatientDataCorrelationRepository.findUserPatientDataCorrelationByUserId(userId).orElse(null);
        if (userPatientDataCorrelation != null) {
            return userPatientDataCorrelation.getUserPrivateDataId();
        }
        return 0;
    }

}
