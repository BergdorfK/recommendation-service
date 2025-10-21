package com.starbank.recommendation_service.repository;

import com.starbank.recommendation_service.model.UserH2;

import java.util.Optional;

public interface UserH2Repository {
    Optional<UserH2> findByUsername(String username);
    Optional<UserH2> findByUserId(String userId);
}