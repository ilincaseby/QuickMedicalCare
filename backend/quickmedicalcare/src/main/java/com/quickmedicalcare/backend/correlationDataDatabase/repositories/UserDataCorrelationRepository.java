package com.quickmedicalcare.backend.correlationDataDatabase.repositories;

import com.quickmedicalcare.backend.correlationDataDatabase.entities.UserDataCorrelation;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository("userPatientDataCorrelationRepository")
public interface UserDataCorrelationRepository extends CrudRepository<UserDataCorrelation, Long> {
    Optional<UserDataCorrelation> findUserPatientDataCorrelationByUserId(long userId);
}
