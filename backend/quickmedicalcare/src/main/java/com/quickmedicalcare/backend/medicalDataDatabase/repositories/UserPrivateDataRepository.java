package com.quickmedicalcare.backend.medicalDataDatabase.repositories;

import com.quickmedicalcare.backend.medicalDataDatabase.entities.UserPrivateData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("medicalPatientDataRepository")
public interface UserPrivateDataRepository extends JpaRepository<UserPrivateData, Long> {

}
