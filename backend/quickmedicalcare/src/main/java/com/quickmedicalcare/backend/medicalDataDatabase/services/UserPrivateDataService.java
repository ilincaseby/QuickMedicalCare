package com.quickmedicalcare.backend.medicalDataDatabase.services;

import com.quickmedicalcare.backend.changeInfo.ChangeInfoPayload;
import com.quickmedicalcare.backend.medicalDataDatabase.entities.UserPrivateData;
import com.quickmedicalcare.backend.registerLogin.RegisterPayload;
import com.quickmedicalcare.backend.medicalDataDatabase.repositories.UserPrivateDataRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserPrivateDataService {
    private final UserPrivateDataRepository patientDataRepository;

    public Long savePatient(RegisterPayload registerPayload) {
        return patientDataRepository.save(new UserPrivateData(registerPayload.getAge(),
                registerPayload.getWeight(), registerPayload.getHeight(),
                registerPayload.getSex(), registerPayload.getCountry(),
                registerPayload.isSmoker(), registerPayload.getAlcoholConsumptionFreq())).getId();
    }

    public Boolean changeCredentials(ChangeInfoPayload changeInfoPayload, Long patientId) {
        UserPrivateData patientData = patientDataRepository.findById(patientId).orElse(null);
        if (patientData == null) {
            return false;
        }
        if (changeInfoPayload.getAge() != null) {
            patientData.setAge(changeInfoPayload.getAge());
        }
        if (changeInfoPayload.getWeight() != null) {
            patientData.setWeight(changeInfoPayload.getWeight());
        }
        if (changeInfoPayload.getHeight() != null) {
            patientData.setHeight(changeInfoPayload.getHeight());
        }
        if (changeInfoPayload.getSex() != null) {
            patientData.setSex(changeInfoPayload.getSex());
        }
        if (changeInfoPayload.getCountry() != null) {
            patientData.setCountry(changeInfoPayload.getCountry());
        }
        if (changeInfoPayload.getSmoker() != null) {
            patientData.setSmoker(changeInfoPayload.getSmoker());
        }
        if (changeInfoPayload.getAlcoholConsumptionFreq() != null) {
            patientData.setAlcoholConsumptionFreq(changeInfoPayload.getAlcoholConsumptionFreq());
        }
        return patientDataRepository.save(patientData) != null;
    }

    public UserPrivateData getPatientById(Long patientId) {
        return patientDataRepository.findById(patientId).orElse(null);
    }
}
