package com.quickmedicalcare.backend.correlationDataDatabase.repositories;

import com.quickmedicalcare.backend.correlationDataDatabase.entities.UserPatientDataCorrelation;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository("userPatientDataCorrelationRepository")
public interface UserPatientDataCorrelationRepository extends CrudRepository<UserPatientDataCorrelation, Long> {
    Optional<UserPatientDataCorrelation> findUserPatientDataCorrelationByUserId(long userId);
}
