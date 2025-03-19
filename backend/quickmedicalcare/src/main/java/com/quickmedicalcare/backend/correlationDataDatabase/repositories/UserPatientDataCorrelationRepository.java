package com.quickmedicalcare.backend.correlationDataDatabase.repositories;

import com.quickmedicalcare.backend.correlationDataDatabase.entities.UserPatientDataCorrelation;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository("userPatientDataCorrelationRepository")
public interface UserPatientDataCorrelationRepository extends CrudRepository<UserPatientDataCorrelation, Long> {
}
