package com.quickmedicalcare.backend.correlationDataDatabase.services;

import com.quickmedicalcare.backend.correlationDataDatabase.entities.UserPatientDataCorrelation;
import com.quickmedicalcare.backend.correlationDataDatabase.repositories.UserPatientDataCorrelationRepository;
import org.springframework.stereotype.Service;

@Service
public class UserPatientDataCorrelationService {
    private UserPatientDataCorrelationRepository userPatientDataCorrelationRepository;

    public void saveUserPatientDataCorrelation(Long userId, Long patientId) {
        userPatientDataCorrelationRepository.save(new UserPatientDataCorrelation(userId, patientId));
    }
}
