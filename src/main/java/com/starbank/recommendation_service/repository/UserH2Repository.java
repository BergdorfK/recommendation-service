package com.starbank.recommendation_service.repository;

import com.starbank.recommendation_service.model.UserH2;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserH2Repository extends JpaRepository<UserH2, Long> {
    Optional<UserH2> findByUsername(String username);
    Optional<UserH2> findByUserId(String userId); // Может понадобиться для поиска по UUID
}