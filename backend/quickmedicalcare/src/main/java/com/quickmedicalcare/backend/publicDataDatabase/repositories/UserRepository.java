package com.quickmedicalcare.backend.publicDataDatabase.repositories;

import com.quickmedicalcare.backend.publicDataDatabase.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository("publicUserRepository")
//@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
    Optional<User> findByUserIdSuperTokens(String userIdSuperTokens);
}
