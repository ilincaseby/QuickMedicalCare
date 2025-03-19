package com.quickmedicalcare.backend.medicalDataDatabase.repositories;

import com.quickmedicalcare.backend.medicalDataDatabase.entities.PatientData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("medicalPatientDataRepository")
public interface PatientDataRepository extends JpaRepository<PatientData, Long> {

}
