package com.quickmedicalcare.backend.medicalDataDatabase.services;

import com.quickmedicalcare.backend.RegisterLogin.RegisterPayload;
import com.quickmedicalcare.backend.medicalDataDatabase.entities.PatientData;
import com.quickmedicalcare.backend.medicalDataDatabase.repositories.PatientDataRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PatientDataService {
    private PatientDataRepository patientDataRepository;

    public Long savePatient(RegisterPayload registerPayload) {
        return patientDataRepository.save(new PatientData(registerPayload.getAge(),
                registerPayload.getWeight(), registerPayload.getHeight(),
                registerPayload.getSex(), registerPayload.getCountry(),
                registerPayload.isSmoker(), registerPayload.getAlcoholConsumptionFreq())).getId();
    }
}
